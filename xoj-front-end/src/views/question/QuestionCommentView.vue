<template>
  <div v-for="(item, index) in items" :key="index">
    <a-comment
      :author="item.userVO?.userName"
      :datetime="moment(item.createTime).format('YY-MM-DD HH:mm')"
      align="right"
      class="userComment"
    >
      <template #actions>
        <span class="action" @click="onLikeChange(item)">
          <span v-if="item.like">
            <IconHeartFill :style="{ color: '#f53f3f' }" />
          </span>
          <span v-else>
            <IconHeart />
          </span>
          {{ item.thumbNum }}
        </span>
        <span
          v-if="
            item.userId === loginUser.id ||
            loginUser.userRole === ACCESS_ENUM.ADMIN
          "
          class="deleteComment"
          @click="deleteComment(item.id as number)"
        >
          <IconDelete /> 删除
        </span>
      </template>
      <template #avatar>
        <a-avatar>
          <img :alt="item.userVO?.userName" :src="item.userVO?.userAvatar" />
        </a-avatar>
      </template>
      <template #content>
        <div>{{ item.comment }}</div>
      </template>
    </a-comment>
  </div>
  <a-pagination
    :total="total"
    show-total
    :pageSize="searchParams.pageSize"
    :current="searchParams.current"
    @change="onPageChange"
    style="margin-top: 10px"
  />
  <a-comment align="right" :avatar="store.state.user.loginUser.userAvatar">
    <template #actions>
      <a-button key="0" type="secondary"> Cancel</a-button>
      <a-button key="1" type="primary" @click="submitComment"> 发布</a-button>
    </template>
    <template #content>
      <a-textarea
        placeholder="请输入你的评论"
        v-model="comment"
        allow-clear
        :auto-size="{
          minRows: 3,
        }"
      />
    </template>
  </a-comment>
</template>
<script setup lang="ts">
import {
  onMounted,
  ref,
  withDefaults,
  defineProps,
  reactive,
  watchEffect,
} from "vue";
import { QuestionQueryRequest } from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { CommentControllerService } from "../../../generated/services/CommentControllerService";
import { CommentVO } from "../../../generated/models/CommentVO";
import { useStore } from "vuex";
import { CommentUpdateThumbNumRequest } from "../../../generated/models/CommentUpdateThumbNumRequest";
import moment from "moment";
import ACCESS_ENUM from "@/access/accessEnum";

const store = useStore();

interface Props {
  id: string;
}

const props = withDefaults(defineProps<Props>(), {
  id: () => "",
});

//评论

const loginUser = store.state.user.loginUser;

const commentData = reactive<CommentVO>({
  comment: "",
  thumbNum: 0,
  createTime: "",
  id: 0,
  like: false,
});

let items = ref([commentData]);

const total = ref(0);

const searchParams = ref<QuestionQueryRequest>({
  current: 1,
  pageSize: 3,
});
//分页获取评论信息
const loadCommentData = async () => {
  const res = await CommentControllerService.listCommentByPageUsingPost({
    ...searchParams.value,
    questionId: props.id as any,
  });
  if (res.code === 0) {
    items.value = res.data.records;
    console.log("dasdasda", items.value);
    total.value = res.data.total;
  } else {
    message.error("评论加载失败" + res.message);
  }
};

//改变点赞状态
const onLikeChange = async (item: any) => {
  item.like = !item.like;
  const commentUpdateThumb = reactive<CommentUpdateThumbNumRequest>({
    like: item.like,
    id: item.id,
  });
  const res = await CommentControllerService.changeCommentThumbNumUsingPost(
    commentUpdateThumb
  );
  if (res.code === 0) {
    item.like = res.data?.status;
    item.thumbNum = res.data?.thumbNum;
    if (item.like) {
      message.success("点赞成功");
    } else {
      message.success("取消成功");
    }
  } else {
    message.error("操作失败" + res.message);
  }
};

const onPageChange = (page: number) => {
  searchParams.value = {
    ...searchParams.value,
    current: page,
  };
};

onMounted(() => {
  loadCommentData();
});

watchEffect(() => {
  loadCommentData();
});

const comment = ref("");

const questionId = ref(props.id as any);

const commentSubmitData = ref({
  comment,
  questionId,
});

const submitComment = async () => {
  if (!commentSubmitData.value.comment) {
    message.error("提交评论不能为空");
    return;
  }
  const res = await CommentControllerService.addCommentUsingPost(
    commentSubmitData.value
  );
  if (res.code === 0) {
    loadCommentData();
  } else {
    message.error("" + res.message);
  }
};

const deleteComment = async (id: number) => {
  const res = await CommentControllerService.deleteCommentUsingPost({
    id,
  });
  if (res.code === 0) {
    message.success("删除成功!");
    loadCommentData();
  } else {
    message.success("删除失败" + res.message);
  }
};
</script>

<style>
#viewQuestionsView .arco-space-horizontal .arco-space-item {
  margin-bottom: 0 !important;
}

.userComment {
  padding: 10px;
  margin-top: 30px;
  background-image: radial-gradient(circle, #74e1ec, #f6f695);
  border-radius: 10px;
}

.deleteComment {
  color: black;
}

.deleteComment:hover {
  color: red;
}
</style>
