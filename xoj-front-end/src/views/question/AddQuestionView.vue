<template>
  <div id="addQuestionView">
    <h2>创建题目</h2>
    <a-form :model="form" label-align="left">
      <a-form-item
        :rules="[
          { minLength: 2, message: '题目标题长度不能少于2位' },
          { maxLength: 32, message: '题目标题长度不能超过32位' },
        ]"
        :validate-trigger="['blur']"
        field="title"
        label="标题"
        required
        style="max-width: 900px"
      >
        <a-input
          v-model="form.title"
          placeholder="请输入标题"
          style="margin-left: 120px"
        />
      </a-form-item>
      <a-form-item
        :rules="[{ empty: false, message: '标签不能为空' }]"
        :validate-trigger="['blur']"
        field="tags"
        label="标签"
        required
        style="max-width: 900px"
      >
        <a-input-tag
          v-model="form.tags"
          allow-clear
          placeholder="请选择标签"
          style="margin-left: 120px"
        />
      </a-form-item>
      <a-form-item
        :rules="[
          { minLength: 8, message: '题目内容长度不能少于8位' },
          { maxLength: 8192, message: '题目标题长度不能超过1000位' },
        ]"
        :validate-trigger="['blur']"
        field="content"
        label="题目内容"
        required
      >
        <MdEditor :handle-change="onContentChange" :value="form.content" />
      </a-form-item>
      <a-form-item field="answer" label="答案">
        <MdEditor :handle-change="onAnswerChange" :value="form.answer" />
      </a-form-item>
      <a-form-item
        :content-flex="false"
        :merge-props="false"
        label="判题配置"
        required
      >
        <a-space direction="vertical" style="min-width: 488px">
          <a-form-item field="judgeConfig.memoryLimit" label="内存限制">
            <a-input-number
              v-model="form.judgeConfig.memoryLimit"
              min="0"
              mode="button"
              placeholder="请输入内存限制"
              size="large"
            />
          </a-form-item>
          <a-form-item field="judgeConfig.timeLimit" label="时间限制">
            <a-input-number
              v-model="form.judgeConfig.timeLimit"
              min="0"
              mode="button"
              placeholder="请输入时间限制"
              size="large"
            />
          </a-form-item>
        </a-space>
      </a-form-item>
      <a-form-item
        :content-flex="false"
        :merge-props="false"
        label="判题用例配置"
        no-style
      >
        <a-form-item
          v-for="(judgeCaseItem, index) of form.judgeCase"
          :key="index"
        >
          <a-space direction="vertical" style="min-width: 488px">
            <a-form-item
              :key="index"
              :field="judgeCaseItem.output"
              :label="`输入用例-${index}`"
            >
              <a-input
                v-model="judgeCaseItem.input"
                placeholder="请输入测试用例"
              />
            </a-form-item>
            <a-form-item
              :key="index"
              :field="judgeCaseItem.output"
              :label="`输出用例-${index}`"
            >
              <a-input
                v-model="judgeCaseItem.output"
                placeholder="请输入测试输出用例"
              />
            </a-form-item>
            <a-button status="danger" @click="handleDelete(index)"
              >删除
            </a-button>
          </a-space>
        </a-form-item>
        <div style="margin-bottom: 13px; margin-left: 307px">
          <a-button status="success" type="outline" @click="handleAdd"
            >新增测试用例
          </a-button>
        </div>
      </a-form-item>
      <a-form-item style="margin-top: 16px">
        <a-button style="min-width: 100px" type="primary" @click="doSubmit"
          >提交
        </a-button>
      </a-form-item>
    </a-form>
  </div>
</template>
<script lang="ts" setup>
import { onMounted, ref } from "vue";
import MdEditor from "@/components/MdEditor.vue";
import { QuestionControllerService } from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";

let form = ref({
  answer: "",
  content: "",
  judgeCase: [
    {
      input: "",
      output: "",
    },
  ],
  judgeConfig: {
    memoryLimit: 1000000,
    timeLimit: 1000,
  },
  tags: [],
  title: "",
});

const router = useRouter();
//如果路由路径包含update视为更新路由
const updatePage = router.currentRoute.value.path.includes("update");

//根据题目id 获取老的数据
const loadData = async () => {
  const id = router.currentRoute.value.query.id;
  if (!id) {
    return;
  }
  const res = await QuestionControllerService.getQuestionByIdUsingGet(
    id as any
  );
  if (res.code === 0) {
    form.value = res.data as any;
    if (!form.value.judgeCase) {
      form.value.judgeCase = [
        {
          input: "",
          output: "",
        },
      ];
    } else {
      form.value.judgeCase = JSON.parse(form.value.judgeCase as any);
    }
    if (!form.value.judgeConfig) {
      form.value.judgeConfig = {
        memoryLimit: 0,
        timeLimit: 0,
      };
    } else {
      form.value.judgeConfig = JSON.parse(form.value.judgeConfig as any);
    }
    if (!form.value.tags) {
      form.value.tags = [];
    } else {
      form.value.tags = JSON.parse(form.value.tags as any);
    }
  } else {
    message.error("加载失败" + res.message);
  }
};

onMounted(() => {
  loadData();
});
const handleAdd = () => {
  form.value.judgeCase.push({
    input: "",
    output: "",
  });
};
const handleDelete = (index: number) => {
  form.value.judgeCase.splice(index, 1);
};

const doSubmit = async () => {
  if (!form.value.title || form.value.title.length > 80) {
    message.error("标题为空或输入不符合规范");
    return;
  }
  if (!form.value.tags.entries()) {
    message.error("请输入标签");
    return;
  }
  if (!form.value.content || form.value.content.length > 8192) {
    message.error("题目内容为空或输入不符合规范");
    return;
  }
  if (!form.value.answer && form.value.answer.length > 8192) {
    message.error("答案过长");
    return;
  }
  if (!form.value.judgeCase) {
    message.error("请输入判题用例");
    return;
  }
  if (!form.value.judgeConfig) {
    message.error("请输入判题配置");
    return;
  }
  if (updatePage) {
    const res = await QuestionControllerService.updateQuestionUsingPost(
      form.value
    );
    if (res.code === 0) {
      message.success("更新题目成功");
    } else {
      message.error("更新题目失败" + res.message);
    }
  } else {
    const res = await QuestionControllerService.addQuestionUsingPost(
      form.value
    );
    if (res.code === 0) {
      message.success("创建题目成功");
      form.value = {
        answer: "",
        content: "",
        judgeCase: [
          {
            input: "",
            output: "",
          },
        ],
        judgeConfig: {
          memoryLimit: 1000000,
          timeLimit: 1000,
        },
        tags: [],
        title: "",
      };
    } else {
      message.error("创建题目失败" + res.message);
    }
  }
};

const onContentChange = (value: string) => {
  form.value.content = value;
};
const onAnswerChange = (value: string) => {
  form.value.answer = value;
};
</script>

<style scoped>
#addQuestionView {
}
</style>
