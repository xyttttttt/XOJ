<template>
  <h1>我的提交记录</h1>
  <a-table
    :columns="columns"
    :data="dataList"
    style="margin-top: 20px"
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
        <a-tag color="#0fc6c2">{{ record.language }}</a-tag>
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
    <template #createTime="{ record }">
      {{ moment(record.createTime).format("YYYY-MM-DD HH:mm") }}
    </template>
  </a-table>
</template>
<script setup lang="ts">
import { useStore } from "vuex";
import { onMounted, ref, watchEffect } from "vue";
import { QuestionControllerService, User } from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";
import moment from "moment/moment";
import { QuestionSubmitByMyQueryRequest } from "../../../generated/models/QuestionSubmitByMyQueryRequest";

const store = useStore();
const router = useRouter();
const userName: string = store.state.user.loginUser.userName;
const form = ref<User>({});

const dataList = ref([]);

const total = ref(0);

const searchParams = ref<QuestionSubmitByMyQueryRequest>({
  userId: store.state.user.loginUser.id,
  language: "",
  pageSize: 10,
  current: 1,
});
const loadData2 = async () => {
  const res =
    await QuestionControllerService.listQuestionSubmitByMyPageUsingPost({
      ...searchParams.value,
      sortField: "createTime",
      sortOrder: "descend",
    });
  if (res.code === 0) {
    dataList.value = res.data.records;
    total.value = res.data.total;
  } else {
    message.error("加载失败" + res.message);
  }
};
watchEffect(() => {
  loadData2();
});
onMounted(() => {
  loadData2();
});

const columns = [
  {
    title: "提交时间",
    slotName: "createTime",
  },
  {
    title: "题号",
    dataIndex: "questionId",
  },
  {
    title: "题目名称",
    dataIndex: "title",
  },
  {
    title: "语言",
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
];
const onPageChange = (page: number) => {
  searchParams.value = {
    ...searchParams.value,
    current: page,
  };
};
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
  "Output Limit Exceeded": { text: "输出溢出", color: "#ff7d00" },
  "Presentation Error": { text: "展示错误", color: "#0fc6c2" },
  Waiting: { text: "等待中", color: "#168cff" },
  "System Error": { text: "系统错误", color: "#86909c" },
  "Language UnSupported": { text: "语言不支持", color: "#0fc6c2" },
  "Sandbox System Error": { text: "沙箱系统错误", color: "#0fc6c2" },
  default: { text: "未知错误", color: "#86909c" },
};
</script>

<style scoped>
.menu-demo .arco-menu {
  width: 200px;
  height: 100%;
  box-shadow: 0 0 1px rgba(0.3, 0, 0, 0.5);
}

.menu-demo .arco-menu :deep(.arco-menu-collapse-button) {
  width: 32px;
  height: 32px;
  border-radius: 50%;
}

.menu-demo
  .arco-menu:not(.arco-menu-collapsed)
  :deep(.arco-menu-collapse-button) {
  right: 0;
  bottom: 8px;
  transform: translateX(50%);
}

.menu-demo .arco-menu:not(.arco-menu-collapsed)::before {
  content: "";
  position: absolute;
  right: 0;
  bottom: 0;
  width: 48px;
  height: 48px;
  background-color: inherit;
  border-radius: 50%;
  box-shadow: -4px 0 2px var(--color-bg-2), 0 0 1px rgba(0, 0, 0, 0.3);
  transform: translateX(50%);
}

.menu-demo .arco-menu.arco-menu-collapsed {
  width: 48px;
  height: auto;
  padding-top: 24px;
  padding-bottom: 138px;
  border-radius: 22px;
}

.menu-demo .arco-menu.arco-menu-collapsed :deep(.arco-menu-collapse-button) {
  right: 8px;
  bottom: 8px;
}
</style>
