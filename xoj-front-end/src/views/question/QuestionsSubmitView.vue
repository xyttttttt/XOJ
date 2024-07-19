<template>
  <div id="questionsSubmitView">
    <a-form :model="searchParams" layout="inline">
      <a-form-item field="questionId" label="题号" style="min-width: 240px">
        <a-input v-model="searchParams.questionId" placeholder="请输入" />
      </a-form-item>
      <a-form-item field="language" label="语言" style="min-width: 240px">
        <a-select
          v-model="searchParams.language"
          :style="{ width: '200px' }"
          default-value="java"
          placeholder="选择编程语言"
        >
          <a-option>java</a-option>
          <a-option>cpp</a-option>
          <a-option>python</a-option>
        </a-select>
      </a-form-item>
      <a-form-item>
        <a-button type="primary" @click="doSubmit">
          搜索
          <icon-search />
        </a-button>
      </a-form-item>
      <a-form-item>
        <a-button type="primary" @click="remove">
          清除
          <icon-close />
        </a-button>
      </a-form-item>
    </a-form>
    <a-divider size="0"></a-divider>
    <a-table
      :column-resizable="true"
      :ref="tableRef"
      :columns="columns"
      :data="dataList"
      :pagination="{
        showTotal: true,
        pageSize: searchParams.pageSize,
        current: searchParams.current,
        total,
        showJumper: true,
        showPageSize: true,
      }"
      @page-change="onPageChange"
      @pageSizeChange="onPageSizeChange"
    >
      <template #language="{ record }">
        <a-space wrap>
          <a-tag color="#ec05a1">{{ record.language }}</a-tag>
        </a-space>
      </template>
      <template #result="{ record }">
        <template v-if="record.status == 0 || record.status == 1">
          <a-tag size="large" loading>Loading</a-tag>
        </template>
        <template
          v-else-if="
            record.judgeInfo.message !== undefined &&
            record.judgeInfo.message !== null &&
            record.judgeInfo.message !== '' &&
            judgeResultObjtList[record.judgeInfo.message] !== undefined &&
            judgeResultObjtList[record.judgeInfo.message] !== null
          "
        >
          <a-tag size="large" color="#cccccc">
            {{ judgeResultObjtList[record.judgeInfo.message].text }}
          </a-tag>
        </template>
        <template
          v-else-if="
            record.judgeInfo.message !== undefined &&
            record.judgeInfo.message !== null &&
            record.judgeInfo.message !== ''
          "
        >
          <a-tag size="large" color="#0fc6c2">
            {{ record.judgeInfo.message }}
          </a-tag>
        </template>
        <template v-else>
          <span>{{ record.judgeInfo.message }}</span>
        </template>
      </template>
      <template #memory="{ record }">
        <template v-if="record.judgeInfo.memory <= 1024">
          <span>{{ record.judgeInfo.memory }} byte</span>
        </template>
        <template v-else-if="record.judgeInfo.memory <= 1024 * 1024">
          <span>{{ (record.judgeInfo.memory / 1024).toFixed(2) }} KB</span>
        </template>
        <template v-else>
          <span
            >{{ (record.judgeInfo.memory / (1024 * 1024)).toFixed(2) }} MB</span
          >
        </template>
      </template>
      <template #time="{ record }">
        <template v-if="record.judgeInfo.time < 1000">
          <span>{{ record.judgeInfo.time }} MS</span>
        </template>
        <template v-else>
          <span>{{ (record.judgeInfo.time / 1000).toFixed(2) }} S</span>
        </template>
      </template>
      <!-- 判题状态 -->
      <template #status="{ record }">
        <span :style="getJudgeStatusStyle(record.status)">{{
          judgeStatusObjtList[record.status].text
        }}</span>
      </template>
      <template #createTime="{ record }">
        {{ moment(record.createTime).format("YYYY-MM-DD HH:mm") }}
      </template>
    </a-table>
  </div>
</template>
<script setup lang="ts">
import { onMounted, ref, watchEffect } from "vue";
import {
  QuestionControllerService,
  QuestionSubmitQueryRequest,
} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import moment from "moment";

const tableRef = ref();
const onPageChange = (page: number) => {
  searchParams.value = {
    ...searchParams.value,
    current: page,
  };
};
/**
 * 分页大小
 * @param size
 */
const onPageSizeChange = (size: number) => {
  searchParams.value = {
    ...searchParams.value,
    pageSize: size,
  };
};
const judgeStatusObjtList = [
  { text: "等待中", color: "#168cff" },
  { text: "判题中", color: "#ffb400" },
  { text: "成功", color: "#0bf196" },
  { text: "失败", color: "#f53f3f" },
];
const getJudgeStatusStyle = (judgeStatus: number) => {
  if (
    judgeStatus == undefined ||
    judgeStatus == null ||
    judgeStatusObjtList[judgeStatus] == undefined ||
    judgeStatusObjtList[judgeStatus] == null
  ) {
    return `color: #86909c;font-weight: bold;`;
  }
  const color = judgeStatusObjtList[judgeStatus].color;
  return `color: ${color};font-weight: bold;`;
};

const judgeResultObjtList = {
  Accepted: { text: `成功`, color: "#00b42a" },
  "Wrong Answer": { text: "答案错误", color: "#f53f3f" },
  "Runtime Error": { text: "运行错误", color: "#f53f3f" },
  "Dangerous Operation": { text: "危险操作", color: "#f53f3f" },
  "Compile Error": { text: "编译错误", color: "#ffb400" },
  "Time Limit Exceeded": { text: "超时", color: "#0fc6c2" },
  "Memory Limit Exceeded": { text: "内存溢出", color: "#ff7d00" },
  "Out Of Memory": { text: "内存不足", color: "#ec05a3" },
  Waiting: { text: "等待中", color: "#168cff" },
  "System Error": { text: "系统错误", color: "#86909c" },
  default: { text: "未知错误", color: "#86909c" },
};

const dataList = ref([]);

const total = ref(0);
const searchParams = ref<QuestionSubmitQueryRequest>({
  questionId: undefined,
  language: "",
  pageSize: 10,
  current: 1,
});

const loadData = async () => {
  const res = await QuestionControllerService.listQuestionSubmitByPageUsingPost(
    {
      ...searchParams.value,
      sortField: "createTime",
      sortOrder: "descend",
    }
  );
  if (res.code === 0) {
    dataList.value = res.data.records;
    total.value = res.data.total;
  } else {
    message.error("加载失败" + res.message);
  }
};

/**
 * 监听searchParams变量的值，当发送改变时重新渲染页面
 * */
watchEffect(() => {
  loadData();
});

onMounted(() => {
  loadData();
});

const doSubmit = () => {
  searchParams.value = {
    ...searchParams.value,
    current: 1,
  };
  loadData();
};

//[{id: "1", title: "A + B", content: "title", tags: ["二叉树"],…},…], total: "3", size: "10",…
const columns = [
  {
    title: "提交号",
    dataIndex: "id",
  },
  {
    title: "编程语言",
    slotName: "language",
  },
  {
    title: "判题信息",
    children: [
      {
        title: "判题结果",
        slotName: "result",
        align: "left",
      },
      {
        title: "消耗内存",
        slotName: "memory",
        align: "left",
      },
      {
        title: "执行用时",
        slotName: "time",
        align: "left",
      },
    ],
  },
  {
    title: "判题状态",
    slotName: "status",
    align: "center",
  },
  {
    title: "题目Id",
    dataIndex: "questionId",
  },
  {
    title: "提交者Id",
    dataIndex: "userId",
  },
  {
    title: "创建时间",
    slotName: "createTime",
  },
];

const remove = () => {
  searchParams.value.language = "";
  searchParams.value.questionId = undefined;
  loadData();
};
</script>

<style scoped>
#questionsSubmitView {
  width: 1400px;
  margin: 0 auto;
}
</style>
