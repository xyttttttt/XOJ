<template>
  <div id="viewQuestionsView">
    <a-row :gutter="[24, 24]">
      <a-col :md="12" :xs="24">
        <a-tabs default-active-key="question" lazy-load>
          <a-tab-pane key="question" title="题目">
            <a-card v-if="question" :title="question.title">
              <a-descriptions
                title="判题条件:"
                :column="{ xs: 1, md: 2, lg: 3 }"
              >
                <a-descriptions-item label="时间限制（ms）：">
                  {{ question.judgeConfig.timeLimit ?? 0 }}
                </a-descriptions-item>
                <a-descriptions-item label="内存限制（KB）：">
                  {{ question.judgeConfig.memoryLimit ?? 0 }}
                </a-descriptions-item>
                <a-descriptions-item label="堆栈限制（KB）：">
                  {{ question.judgeConfig.stackLimit ?? 0 }}
                </a-descriptions-item>
              </a-descriptions>
              <MdViewer :value="question.content || ''" />
              <template #extra>
                <a-space wrap>
                  <div @click="doThumb">
                    <span v-if="!hasThumb">
                      <icon-thumb-up />
                    </span>
                    <span v-else>
                      <icon-thumb-up-fill :style="{ color: '#f53f3f' }" />
                    </span>
                    {{ thumbNum }}
                  </div>
                  <div @click="doFavour">
                    <span v-if="!hasFavour">
                      <icon-star />
                    </span>
                    <span v-else>
                      <icon-star-fill :style="{ color: '#f3e708' }" />
                    </span>
                    {{ favourNum }}
                  </div>
                  <a-tag
                    v-for="(tag, index) of question.tags"
                    :key="index"
                    color="green"
                    >{{ tag }}
                  </a-tag>
                </a-space>
              </template>
            </a-card>
          </a-tab-pane>
          <a-tab-pane key="comment" title="评论">
            <QuestionCommentView :id="id"></QuestionCommentView>
          </a-tab-pane>
        </a-tabs>
      </a-col>
      <a-col :md="12" :xs="24">
        <div id="right-clo">
          <div class="codeEditor">
            <a-form :model="form" layout="inline">
              <a-form-item
                field="language"
                label="语言"
                style="min-width: 240px"
              >
                <a-select
                  v-model="form.language"
                  :style="{ width: '320px' }"
                  default-value="java"
                  placeholder="选择编程语言"
                >
                  <a-option>java</a-option>
                  <a-option>cpp</a-option>
                  <a-option>python</a-option>
                </a-select>
              </a-form-item>
            </a-form>
            <CodeEditor
              :value="form.code as string"
              :language="form.language"
              :handle-change="changeCode"
            />
          </div>
          <a-divider size="0" />
          <a-button
            type="primary"
            :loading="loading"
            class="submit-button"
            @click="doSubmit"
            >提交代码
          </a-button>
          <a-button
            class="result-button"
            v-if="showResult"
            type="primary"
            @click="handleClick"
          >
            查看结果
          </a-button>
          <a-drawer
            :width="340"
            :height="320"
            :visible="visible"
            placement="bottom"
            popup-container="#right-clo"
            @ok="handleOk"
            @cancel="handleCancel"
            unmountOnClose
          >
            <template #title>执行结果</template>
            <div class="judge-result">
              判题结果：{{ judgeInfo.message }}<br />
              <a-divider />
              占用内存：{{ judgeInfo.memory }}<br />
              <a-divider />
              执行时间：{{ judgeInfo.time }}<br />
              <a-divider />
            </div>
          </a-drawer>
        </div>
      </a-col>
    </a-row>
  </div>
</template>
<script setup lang="ts">
import { onMounted, ref, withDefaults, defineProps, reactive } from "vue";
import {
  JudgeInfo,
  QuestionControllerService,
  QuestionFavourControllerService,
  QuestionSubmitAddRequest,
  QuestionThumbControllerService,
  QuestionVO,
} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import CodeEditor from "@/components/CodeEditor.vue";
import MdViewer from "@/components/MdViewer.vue";
import QuestionCommentView from "@/views/question/QuestionCommentView.vue";

const question = ref<QuestionVO>();
const thumbNum = ref<number>(0);
const favourNum = ref<number>(0);

const hasThumb = ref<boolean>(false);
const hasFavour = ref<boolean>(false);

interface Props {
  id: string;
}

const props = withDefaults(defineProps<Props>(), {
  id: () => "",
});

const form = ref<QuestionSubmitAddRequest>({
  language: "java",
  code:
    "public class Main {\n" +
    "    public static void main(String[] args) {\n" +
    "        int a = Integer.parseInt(args[0]);\n" +
    "        int b = Integer.parseInt(args[1]);\n" +
    "        System.out.println(a + b);\n" +
    "    }\n" +
    "}\n",
});

const changeCode = (value: string) => {
  form.value.code = value;
};
//判题结果
const judgeInfo = ref<JudgeInfo>({
  message: "",
  memory: 0,
  time: 0,
});
//是否展示 查看结果按钮
const showResult = ref(false);

//是否显示抽屉
const visible = ref(false);

const questionSubmit = reactive({
  questionSubmitId: 0,
});
/**
 * 提交代码
 * */
const loading = ref(false);
const doSubmit = async () => {
  if (!question.value?.id) {
    message.error("获取题目异常");
    return;
  }
  if (!form.value.code) {
    message.error("代码为空，请编写代码后提交");
    return;
  }
  loading.value = !loading.value;
  setTimeout(() => {
    loading.value = !loading.value;
  }, 2500);
  const res = await QuestionControllerService.doSubmitQuestionUsingPost({
    ...form.value,
    questionId: question.value?.id,
  });
  if (res.code === 0) {
    message.success("提交成功");
    questionSubmit.questionSubmitId = res.data;
    // judgeInfo.value = res.data.judgeInfo;
    showResult.value = true;
  } else {
    message.error("提交失败" + res.message);
  }
};
//点击显示抽屉
const handleClick = async () => {
  let questionSubmitId = questionSubmit.questionSubmitId;
  visible.value = true;
  const res = await QuestionControllerService.getQuestionSubmitInfoByIdUsePost({
    questionSubmitId,
  });
  if (res.code === 0) {
    judgeInfo.value = res.data.judgeInfo;
  } else {
    message.info("判题中请稍后");
  }
};
//点击确认回调
const handleOk = () => {
  visible.value = false;
};
//点击取消回调
const handleCancel = () => {
  visible.value = false;
};

const loadData = async () => {
  const res = await QuestionControllerService.getQuestionVoByIdUsingGet(
    props.id as any
  );
  if (res.code === 0) {
    question.value = res.data;
    thumbNum.value = res.data?.thumbNum;
    favourNum.value = res.data?.favourNum;
    hasThumb.value = res.data?.hasThumb;
    hasFavour.value = res.data?.hasFavour;
  } else {
    message.error("题目信息加载失败" + res.message);
  }
};

onMounted(() => {
  loadData();
});

//点赞

const doThumb = async () => {
  const res = await QuestionThumbControllerService.doThumbUsingPost({
    questionId: question.value?.id,
  });
  if (res.code === 0) {
    thumbNum.value = res.data.thumbNum;
    if (res.data.status) {
      message.success("点赞成功");
      hasThumb.value = true;
    } else {
      message.success("取消成功");
      hasThumb.value = false;
    }
  } else {
    message.error("" + res.message);
  }
};

//收藏

const doFavour = async () => {
  const res = await QuestionFavourControllerService.doPostFavourUsingPost({
    questionId: question.value?.id,
  });
  if (res.code === 0) {
    favourNum.value = res.data.favourNum;
    if (res.data.status) {
      message.success("收藏成功");
      hasFavour.value = true;
    } else {
      message.success("取消成功");
      hasFavour.value = false;
    }
  } else {
    message.error("" + res.message);
  }
};
</script>

<style>
#viewQuestionsView {
  width: 1400px;
  margin: 0 auto;
}

.codeEditor {
  background-color: #fafafa;
  position: relative;
  z-index: 0;
  width: 600px;
  height: 570px;
  margin: 20px;
  border-radius: 10px;
  overflow: hidden;
  padding: 2rem;

  &::before {
    content: "";
    position: absolute;
    z-index: -2;
    left: -50%;
    top: -50%;
    width: 200%;
    height: 200%;
    background-color: #1a232a;
    background-repeat: no-repeat;
    background-position: 0 0;
    background-image: conic-gradient(
      transparent,
      rgba(168, 239, 255, 1),
      transparent 70%
    );
    animation: rotate 4s linear infinite;
  }

  &::after {
    content: "";
    position: absolute;
    z-index: -1;
    left: 6px;
    top: 6px;
    width: calc(100% - 12px);
    height: calc(100% - 12px);
    background: #1a342a;
    border-radius: 5px;
  }
}

.codeEditor::after {
  animation: opacityChange 10s infinite linear;
}

@keyframes opacityChange {
  50% {
    opacity: 0;
  }
  100% {
    opacity: 1;
  }
}

.arco-tabs-nav-type-line .arco-tabs-tab {
  margin: 0 16px;
  padding: 8px 0;
  line-height: 1.5715;
  color: #09e5de;
}

.arco-form-item-label-col > .arco-form-item-label {
  max-width: 100%;
  font-size: 14px;
  white-space: normal;
  color: var(--color-text-3) !important;
}

.result-button {
  margin-left: 20px;
  min-width: 100px;
}

.submit-button {
  min-width: 100px;
  margin-left: 20px;
}

.judge-result {
  font-size: 16px;
  color: hotpink;
}
</style>
