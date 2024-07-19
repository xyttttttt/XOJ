<template>
  <div class="user-create-list">
    <h1>我的创建</h1>
    <div v-if="dataList">
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
        <template #tags="{ record }">
          <a-space wrap>
            <a-tag
              v-for="(tag, index) of record.tags"
              :key="index"
              color="#0fc6c2"
              >{{ tag }}
            </a-tag>
          </a-space>
        </template>
        <template #acceptedRate="{ record }">
          {{
            `${
              record.acceptedNum
                ? (record.acceptedNum / record.submitNum).toFixed(2)
                : "0"
            }% (${record.acceptedNum}/${record.submitNum})`
          }}
        </template>
        <template #optional="{ record }">
          <a-space>
            <a-button type="primary" @click="doUpdate(record)">修改</a-button>
            <a-button status="danger" @click="doDelete(record)">删除</a-button>
          </a-space>
        </template>
      </a-table>
    </div>
    <div v-else>
      <a-empty />
    </div>
  </div>
</template>
<script setup lang="ts">
import { useStore } from "vuex";
import { useRouter } from "vue-router";
import { onMounted, ref, watchEffect } from "vue";
import {
  Question,
  QuestionControllerService,
  QuestionQueryRequest,
  User,
} from "../../../generated";
import message from "@arco-design/web-vue/es/message";

const store = useStore();
const router = useRouter();
const userName: string = store.state.user.loginUser.userName;
const form = ref<User>({});

const dataList = ref([]);

const total = ref(0);

const searchParams = ref<QuestionQueryRequest>({
  userId: store.state.user.loginUser.id,
  pageSize: 10,
  current: 1,
});
const loadData = async () => {
  const res = await QuestionControllerService.listMyQuestionVoByPageUsingPost({
    ...searchParams.value,
  });
  if (res.code === 0) {
    dataList.value = res.data.records;
    total.value = res.data.total;
  } else {
    message.error("加载失败" + res.message);
  }
};
watchEffect(() => {
  loadData();
});
onMounted(() => {
  loadData();
});

const columns = [
  {
    title: "题号",
    dataIndex: "id",
  },
  {
    title: "题目名称",
    dataIndex: "title",
  },
  {
    title: "标签",
    slotName: "tags",
  },
  {
    title: "通过率",
    slotName: "acceptedRate",
  },
  {
    title: "点赞数",
    dataIndex: "thumbNum",
  },
  {
    title: "收藏数",
    dataIndex: "favourNum",
  },
  {
    slotName: "optional",
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

const doDelete = async (question: Question) => {
  const res = await QuestionControllerService.deleteQuestionUsingPost({
    id: question.id,
  });
  if (res.code === 0) {
    message.success("删除成功");
    loadData();
  } else {
    message.error("删除失败" + res.message);
  }
};

const doUpdate = (question: Question) => {
  router.push({
    path: "/update/question",
    query: {
      id: question.id,
    },
  });
};
</script>

<style scoped>
.user-create-list {
}
</style>
