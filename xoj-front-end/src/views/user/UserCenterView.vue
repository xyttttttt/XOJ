<template>
  <div id="userCenter">
    <a-row class="grid-demo" style="margin-bottom: 16px">
      <a-col flex="300px">
        <div>
          <a-avatar :size="54" class="smallAvatar">
            <img :src="avatarList.url" />
          </a-avatar>
          <div class="userNameSize">{{ userName }}</div>
          <div class="menu-demo">
            <a-menu mode="vertical" :default-selected-keys="['1']">
              <a-menu-item key="1" @click="changePageStatus(1)">
                <template #icon>
                  <icon-user />
                </template>
                个人资料
              </a-menu-item>
              <a-menu-item key="2" @click="changePageStatus(2)">
                <template #icon>
                  <icon-fire></icon-fire>
                </template>
                我的收藏
              </a-menu-item>
              <a-menu-item key="3" @click="changePageStatus(3)">
                <template #icon>
                  <icon-history />
                </template>
                提交记录
              </a-menu-item>
              <a-menu-item key="5" @click="changePageStatus(4)">
                <template #icon>
                  <icon-sort />
                </template>
                我的创建
              </a-menu-item>
              <a-menu-item key="4" @click="changePageStatus(5)">
                <template #icon>
                  <icon-bar-chart />
                </template>
                进度管理
              </a-menu-item>
            </a-menu>
          </div>
        </div>
      </a-col>
      <a-col flex="auto">
        <div>
          <div v-if="pageStatus === 1" class="first-cart">
            <div class="first-cart-item">
              <div class="bigAvatar">
                <a-space direction="vertical">
                  <a-upload
                    action="/"
                    :fileList="file ? [file] : []"
                    :show-file-list="false"
                    @change="onChange"
                    @progress="onProgress"
                    :custom-request="uploadAvatar"
                  >
                    <template #upload-button>
                      <div
                        class="arco-upload-list-picture custom-upload-avatar"
                        v-if="file && file.url"
                      >
                        <img :src="file.url" />
                        <a-progress
                          v-if="
                            file.status === 'uploading' && file.percent < 100
                          "
                          :percent="file.percent"
                          type="circle"
                          size="large"
                          :style="{
                            position: 'absolute',
                            left: '50%',
                            top: '50%',
                            transform: 'translateX(-50%) translateY(-50%)',
                          }"
                        />
                      </div>
                      <div class="arco-upload-picture-card" v-else>
                        <div class="arco-upload-picture-card-text">
                          <IconPlus />
                          <div style="margin-top: 10px; font-weight: 600">
                            Upload
                          </div>
                        </div>
                      </div>
                    </template>
                  </a-upload>
                </a-space>
              </div>
              <a-form :model="form" size="large" style="margin-top: 40px">
                <a-row :gutter="16">
                  <a-col :span="10">
                    <a-form-item
                      field="value1"
                      label="昵称"
                      label-col-flex="100px"
                    >
                      <a-input v-model="form.userName" />
                    </a-form-item>
                  </a-col>
                  <a-col :span="10">
                    <a-form-item
                      field="value4"
                      label="用户角色"
                      label-col-flex="100px"
                    >
                      <a-input v-model="form.userRole" disabled />
                    </a-form-item>
                  </a-col>
                </a-row>
                <a-row :gutter="16">
                  <a-col :span="10">
                    <a-form-item
                      field="value1"
                      label="账号"
                      label-col-flex="100px"
                    >
                      <a-input v-model="form.userAccount" disabled />
                    </a-form-item>
                  </a-col>
                  <a-col :span="10">
                    <a-form-item
                      field="value2"
                      label="加密密码"
                      label-col-flex="60px"
                      disabled
                    >
                      <a-input-password v-model="form.userPassword" />
                    </a-form-item>
                  </a-col>
                </a-row>
                <a-row :gutter="16">
                  <a-col :span="20">
                    <a-form-item
                      field="value4"
                      label="用户简介"
                      label-col-flex="100px"
                    >
                      <a-textarea
                        placeholder="用户暂时没有留下任何信息~~"
                        allow-clear
                        :auto-size="{
                          minRows: 10,
                          maxRows: 15,
                        }"
                        v-model="form.userProfile"
                      />
                    </a-form-item>
                  </a-col>
                </a-row>
              </a-form>
              <div class="userButton">
                <a-button class="button1" type="primary" @click="updateUser"
                  >保存
                </a-button>
                <a-popconfirm
                  content="确认删除用户吗？一旦删除账号无法恢复"
                  type="warning"
                  @ok="deleteUser"
                >
                  <a-button class="button2" status="danger">注销用户</a-button>
                </a-popconfirm>
              </div>
            </div>
          </div>
          <div v-if="pageStatus === 2">
            <UserFavourListView />
          </div>
          <div v-if="pageStatus === 3">
            <UserSumbitInfo />
          </div>
          <div v-if="pageStatus === 4">
            <UserCreateQuestionView />
          </div>
          <div v-if="pageStatus === 5">
            <UserProcessView />
          </div>
        </div>
      </a-col>
    </a-row>
  </div>
</template>
<script setup lang="ts">
import { useStore } from "vuex";
import { onMounted, ref } from "vue";
import {
  FileControllerService,
  User,
  UserControllerService,
} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";
import { FileItem, Message } from "@arco-design/web-vue";
import { UserAvatarImage } from "../../../generated/models/UserAvatarImage";
import UserProcessView from "@/views/user/UserProcessView.vue";
import UserSumbitInfo from "@/views/user/UserSumbitInfo.vue";
import UserFavourListView from "@/views/user/UserFavourListView.vue";
import UserCreateQuestionView from "@/views/user/UserCreateQuestionView.vue";

const store = useStore();
const router = useRouter();
const userName: string = store.state.user.loginUser.userName;
const pageStatus = ref<number>(1); //1个人资料 2个人收藏 3做题分析 4进度管理
const form = ref<User>({});
let avatarList = ref<UserAvatarImage>({
  name: "",
  url: "",
});
// //用户上传
const file = ref();

const changePageStatus = (value: number) => {
  pageStatus.value = value;
};

/**
 * 上传头像
 */
const uploadAvatar = async () => {
  const res = await FileControllerService.uploadOssFileUsingPost(
    file?.value.file
  );
  if (res.code === 0) {
    avatarList.value = res.data;
    file.value = avatarList.value;
    Message.success("上传成功，点击确认即可修改头像");
  } else {
    Message.error("上传失败！" + res.message);
  }
};
//加载个人资料
const loadData = async () => {
  const res = await UserControllerService.getUserByIdUsingGet(
    store.state.user.loginUser.id
  );
  if (res.code === 0) {
    console.log("初始化页面返回结果", res.data?.userAvatar);
    form.value.id = res.data?.id;
    form.value.userName = res.data?.userName;
    form.value.userAccount = res.data?.userAccount;
    form.value.userRole = res.data?.userRole;
    form.value.userProfile = res.data?.userProfile;
    form.value.userPassword = res.data?.userPassword;
    avatarList.value.url = res.data?.userAvatar as string;
    file.value = avatarList.value;
  } else {
    message.error("加载失败" + res.message);
  }
};

const onChange = (_: never, currentFile: FileItem) => {
  file.value = {
    ...currentFile,
  };
};
const onProgress = (currentFile: FileItem) => {
  file.value = currentFile;
};
//更新用户
const updateUser = async () => {
  const res = await UserControllerService.updateMyUserUsingPost(form.value);
  if (res.code === 0) {
    message.success("修改成功");
  } else {
    message.error("修改失败" + res.message);
  }
};

//删除用户
const deleteUser = async () => {
  const res = await UserControllerService.deleteUserUsingPost(form.value);
  if (res.code === 0) {
    message.success("注销成功");
    localStorage.setItem("token", res.data.token);
    router.push({
      path: "/",
      replace: true,
    });
  } else {
    message.error("注销失败，系统异常" + res.message);
  }
};

onMounted(() => {
  loadData();
});
</script>

<style scoped>
.menu-demo {
  width: 30%;
  height: 450px;
  padding: 40px;
  box-sizing: border-box;
}

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

.userNameSize {
  text-shadow: #4ba4da;
  background-clip: text;
  font-size: 26px;
  margin-left: 80px;
}

.smallAvatar {
  border-style: solid;
  border-width: 2px;
  border-color: #000000;
  margin-left: 95px;
  margin-top: 40px;
  margin-bottom: 10px;
}

.userButton {
  margin-left: 330px;
  padding-top: 20px;
  min-width: 100px;

  .button1 {
    margin-right: 8px;
  }

  .button2 {
    margin-left: 8px;
  }
}

.bigAvatar {
  margin-left: 400px;
  padding-bottom: 20px;
}

.submitInfo {
  float: right;
  font-size: 20px;
}

#userCenter {
}

.first-cart {
}

@property --angle {
  syntax: "<angle>";
  inherits: false;
  initial-value: -45deg;
}

@property --color {
  syntax: "<color>";
  inherits: false;
  initial-value: rgba(255, 255, 255, 1);
}

body {
  width: 100%;
  height: 100%;
  display: flex;
  transform-style: preserve-3d;
  perspective: 500px;
  cursor: pointer;
}

.first-cart {
  padding-top: 50px;
  margin-left: 100px;
  width: 900px;
  height: 650px;
  transform-style: preserve-3d;
  backdrop-filter: blur(15px);
  background: linear-gradient(
    var(--angle),
    rgba(255, 255, 255, 0.2),
    rgba(0, 0, 0, 0.3)
  );
  mask: linear-gradient(var(--angle), var(--color), #fff, #fff);
  box-shadow: 0 20px 40px 1px rgba(0, 0, 0, 0.6),
    inset 0 -10px 20px -5px rgba(0, 0, 0, 0.6),
    0 150px 100px -80px rgba(0, 0, 0, 0.6),
    inset 0 1px 4px hsla(0, 0%, 100%, 0.6),
    inset 0 -1px 1px 1px rgba(0, 0, 0, 0.6);
  transition: all 0.3s, --angle 0.3s;
}

.first-cart-item {
  margin-left: 50px;
}
</style>
