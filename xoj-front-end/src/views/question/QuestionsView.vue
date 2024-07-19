<template>
  <div id="questionsView">
    <a-form :model="searchParams" layout="inline">
      <a-form-item field="title" label="名称" style="min-width: 240px">
        <a-input v-model="searchParams.title" placeholder="请输入名称" />
      </a-form-item>
      <a-form-item field="tags" label="标签" style="min-width: 240px">
        <a-input-tag v-model="searchParams.tags" placeholder="请输入标签" />
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
      :columns="columns"
      :data="dataList"
      :pagination="{
        showTotal: true,
        pageSize: searchParams.pageSize,
        current: searchParams.current,
        total,
      }"
      @page-change="onPageChange"
      @sorter-change="sorterChange"
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
      <template #createTime="{ record }">
        {{ moment(record.createTime).format("YY-MM-DD HH:mm") }}
      </template>
      <template #optional="{ record }">
        <a-space>
          <a-button type="primary" @click="toQuestionPage(record)"
            >做题
          </a-button>
        </a-space>
      </template>
    </a-table>
  </div>
</template>
<script setup lang="ts">
import { onMounted, ref, watchEffect } from "vue";
import {
  Question,
  QuestionControllerService,
  QuestionQueryRequest,
} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";
import moment from "moment";

const onPageChange = (page: number) => {
  searchParams.value = {
    ...searchParams.value,
    current: page,
  };
};

const dataList = ref([]);

const total = ref(0);

const searchParams = ref<QuestionQueryRequest>({
  title: "",
  tags: [],
  pageSize: 10,
  current: 1,
  sortField: "createTime",
  sortOrder: "descend",
});

const loadData = async () => {
  const res = await QuestionControllerService.listQuestionVoByPageUsingPost(
    searchParams.value
  );
  if (res.code === 0) {
    dataList.value = res.data.records;
    console.log("DSAdsadsad", res.data.records);
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

const remove = () => {
  (searchParams.value.title = ""), (searchParams.value.tags = []);
  loadData();
};
const sorterChange = (dataIndex: string) => {
  console.log("dataIndex:", dataIndex);
  if (dataIndex == "favourNum") {
    searchParams.value.sortField = "thumbNum";
  } else {
    searchParams.value.sortField = "favourNum";
  }
  loadData();
};

//[{id: "1", title: "A + B", content: "title", tags: ["二叉树"],…},…], total: "3", size: "10",…
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
    sortable: {
      sortDirections: ["descend"],
    },
  },
  {
    title: "收藏数",
    dataIndex: "favourNum",
    sortable: {
      sortDirections: ["descend"],
    },
  },
  {
    title: "创建时间",
    slotName: "createTime",
  },
  {
    slotName: "optional",
  },
];

const router = useRouter();

//跳转到做题页面
const toQuestionPage = (question: Question) => {
  router.push({
    path: `/view/question/${question.id}`,
  });
};
</script>

<style scoped>
#questionsView {
  width: 1400px;
  margin: 0 auto;
}
</style>
