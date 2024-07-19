<template>
  <h1>收藏列表</h1>
  <a-list size="large" style="max-width: 950px" bordered="true">
    <a-list-item>
      <a-space style="color: darkorange; font-size: 18px">题目序号</a-space>
      <a-space style="margin-left: 280px; color: #09e5de; font-size: 18px"
        >题目标题
      </a-space>
    </a-list-item>
    <div v-if="total != 0">
      <div v-for="(item, index) in questionVOList" :key="index">
        <a-list-item>
          <a-space class="questionId">{{ item.id }}</a-space>
          <a-space class="questionTitle"> {{ item.title }}</a-space>
          <a-button
            type="primary"
            status="danger"
            class="userFavourButton"
            style="margin-left: 5px"
            @click="delQuestionFavour(item.id)"
          >
            <icon-delete />
            删除
          </a-button>
          <a-button
            type="primary"
            class="userFavourButton"
            style="margin-left: 5px"
            @click="toViewQuestionView(item.id)"
          >
            <icon-link />
            去做题
          </a-button>
        </a-list-item>
      </div>
      <a-pagination
        class="userFavourPagination"
        :total="total"
        show-total
        :pageSize="searchParams.pageSize"
        :current="searchParams.current"
        @change="onPageChange"
      />
    </div>
    <div v-else>
      <a-empty />
    </div>
  </a-list>
</template>
<script setup lang="ts">
import {
  QuestionFavourControllerService,
  QuestionVO,
} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import store from "@/store";
import { onMounted, reactive, ref, watchEffect } from "vue";
import { QuestionFavourQueryRequest } from "../../../generated/models/QuestionFavourQueryRequest";
import { useRouter } from "vue-router";

const total = ref(0);
const router = useRouter();

const searchParams = ref<QuestionFavourQueryRequest>({
  current: 1,
  pageSize: 10,
});

const questionVO = reactive<QuestionVO>({
  id: 0,
  title: "",
});
const questionVOList = ref([questionVO]);

const userId = store.state.user.loginUser.id;
const loadData = async () => {
  const res =
    await QuestionFavourControllerService.getUserFavourByPageUsingPost({
      userId,
      ...searchParams.value,
    });
  if (res.code === 0) {
    total.value = res.data.total;
    questionVOList.value = res.data.records;
  } else {
    message.error("展示错误" + res.message);
  }
};

const onPageChange = (page: number) => {
  searchParams.value = {
    ...searchParams.value,
    current: page,
  };
};

onMounted(() => {
  loadData();
});

watchEffect(() => {
  loadData();
});

const toViewQuestionView = (id: any) => {
  router.push({
    path: `/view/question/${id}`,
  });
};

const delQuestionFavour = async (questionId: any) => {
  const res = await QuestionFavourControllerService.doPostFavourUsingPost({
    questionId,
  });
  if (res.code === 0) {
    message.success("删除成功");
    loadData();
  } else {
    message.error("操作失败" + res.message);
  }
};
</script>

<style scoped>
.questionId {
}

.questionTitle {
  margin-left: 200px;
}

.userFavourButton {
  float: right;
}

.userFavourPagination {
  margin-top: 10px;
  float: right;
}

.arco-list {
  color: var(--color-text-1);
}

.arco-scrollbar-container {
  position: relative;
  scrollbar-width: none;
}
</style>
