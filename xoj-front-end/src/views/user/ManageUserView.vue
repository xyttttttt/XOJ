<template>
  <div id="ManageUser">
    <a-form :model="searchParams" layout="inline">
      <a-form-item field="questionId" label="用户账号" style="min-width: 240px">
        <a-input v-model="searchParams.userAccount" placeholder="请输入" />
      </a-form-item>
      <a-form-item field="questionId" label="用户昵称" style="min-width: 240px">
        <a-input v-model="searchParams.userName" placeholder="请输入" />
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
      style="text-align: center"
      :data="dataList"
      :pagination="{
        showTotal: true,
        pageSize: searchParams.pageSize,
        current: searchParams.current,
        total,
      }"
      @page-change="onPageChange"
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
      <template #optional="{ record }">
        <a-space>
          <a-button type="primary" @click="doUpdate(record)">修改权限</a-button>
          <a-button status="danger" @click="doDelete(record)"
            >删除用户
          </a-button>
        </a-space>
      </template>
    </a-table>
    <a-modal
      v-model:visible="visible"
      class="modal-demo"
      @ok="handleOk"
      @cancel="handleCancel"
    >
      <template #title>修改用户权限</template>
      <a-radio-group v-model="checkedUser" direction="vertical">
        <a-radio value="user" @click="changRoleToUser"
          >普通用户（user）
        </a-radio>
        <a-radio value="admin" @click="changRoleToAdmin"
          >管理员（admin）
        </a-radio>
      </a-radio-group>
    </a-modal>
  </div>
</template>
<script setup lang="ts">
import { onMounted, ref, watchEffect } from "vue";
import {
  User,
  UserControllerService,
  UserQueryRequest,
} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import ACCESS_ENUM from "@/access/accessEnum";

const onPageChange = (page: number) => {
  searchParams.value = {
    ...searchParams.value,
    current: page,
  };
};

const dataList = ref([]);

const total = ref(0);

const searchParams = ref<UserQueryRequest>({
  pageSize: 10,
  current: 1,
});

const loadData = async () => {
  const res = await UserControllerService.listUserByPageUsingPost(
    searchParams.value
  );
  if (res.code === 0) {
    dataList.value = res.data.records;
    total.value = res.data.total;
  } else {
    message.error("加载失败" + res.message);
  }
};

const remove = () => {
  (searchParams.value.id = undefined), (searchParams.value.userName = "");
  loadData();
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

const columns = [
  {
    title: "id",
    dataIndex: "id",
    width: 250,
  },
  {
    title: "用户账号",
    dataIndex: "userAccount",
    width: 200,
  },
  {
    title: "用户昵称",
    dataIndex: "userName",
    width: 200,
  },
  {
    title: "用户角色",
    dataIndex: "userRole",
    width: 200,
  },
  {
    title: "操作",
    slotName: "optional",
    width: 200,
  },
];

const doSubmit = () => {
  searchParams.value = {
    ...searchParams.value,
    current: 1,
  };
  loadData();
};
const doDelete = async (user: User) => {
  const res = await UserControllerService.deleteUserUsingPost({
    id: user.id,
  });
  if (res.code === 0) {
    message.success("删除成功");
    loadData();
  } else {
    message.error("删除失败" + res.message);
  }
};
//对话框是否可见
const visible = ref(false);
//选择的用户角色
const changedRole = ref("");

const checkedUser = ref(""); //true 普通用户  false 管理员

const userId = ref<number>();
//点击修改权限按钮
const doUpdate = async (user: User) => {
  userId.value = user.id;
  const res = await UserControllerService.getUserByIdUsingGet(user.id);
  if (res.code === 0) {
    visible.value = true;
    changedRole.value = res.data?.userRole as string;
    if (changedRole.value == "user") {
      checkedUser.value = "user";
    } else {
      checkedUser.value = "admin";
    }
  } else {
    message.error("获取题目信息失败" + res.message);
  }
};
//确认修改权限按钮
const handleOk = async () => {
  visible.value = false;
  let userRole = changedRole.value;
  let id = userId.value;
  const res = await UserControllerService.updateUserUsingPost({
    userRole,
    id,
  });
  if (res.code === 0) {
    message.success("更新用户权限成功");
    loadData();
  } else {
    message.error("更新失败" + res.message);
  }
};
//取消对话框
const handleCancel = () => {
  visible.value = false;
};

const changRoleToUser = () => {
  checkedUser.value = "user";
  changedRole.value = ACCESS_ENUM.USER;
};

const changRoleToAdmin = () => {
  checkedUser.value = "admin";
  changedRole.value = ACCESS_ENUM.ADMIN;
};
</script>
<style scoped>
.modal-demo {
  transition-property: all;
}

.modal-demo:hover {
  transform: translateY(-4px);
}
</style>
