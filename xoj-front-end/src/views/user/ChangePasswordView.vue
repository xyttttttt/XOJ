<template>
  <div id="userRegister">
    <a-space direction="vertical">
      <div class="register-cart">
        <h1 class="register-cart-header">修改密码</h1>
        <a-form :model="form" class="register-cart-content">
          <a-form-item
            field="userAccount"
            label="账号"
            :validate-trigger="['blur']"
            :rules="[
              { minLength: 4, message: '账号长度不能少于4位' },
              { maxLength: 16, message: '账号长度不能超过16位' },
            ]"
          >
            <a-input
              v-model="form.userAccount"
              placeholder="输入你需要修改的账号"
            />
          </a-form-item>
          <a-form-item
            field="userPassword"
            label="旧密码"
            :validate-trigger="['blur']"
            :rules="[
              { minLength: 6, message: '密码长度不能少于6位' },
              { maxLength: 16, message: '密码长度不能超过16位' },
            ]"
          >
            <a-input-password
              v-model="form.userPassword"
              placeholder="请输入旧密码"
            />
          </a-form-item>
          <a-form-item
            field="userPassword"
            label="新密码"
            :validate-trigger="['blur']"
            :rules="[
              { minLength: 6, message: '密码长度不能少于6位' },
              { maxLength: 16, message: '密码长度不能超过16位' },
            ]"
          >
            <a-input-password
              v-model="form.userNewPassword"
              placeholder="请输入新密码"
            />
          </a-form-item>
          <a-form-item
            field="userCheckedPassword"
            label="确认密码"
            style="margin: 0 auto"
          >
            <a-input-password
              v-model="checkPassword"
              placeholder="请确认您的密码"
            />
          </a-form-item>
          <div>
            <a
              style="
                float: right;
                margin-bottom: 16px;
                cursor: pointer;
                color: #1677ff;
              "
              @click="toLogin"
              >返回登录页面</a
            >
          </div>
          <a-form-item>
            <a-button
              type="primary"
              html-type="submit"
              class="button-submit"
              @click="changePassword"
              >确认修改
            </a-button>
          </a-form-item>
        </a-form>
      </div>
    </a-space>
  </div>
</template>
<script setup lang="ts">
import { reactive, ref } from "vue";
import { UserControllerService, UserUpdateRequest } from "../../../generated";
import { useRouter } from "vue-router";
import message from "@arco-design/web-vue/es/message";

const router = useRouter();
const form = reactive<UserUpdateRequest>({
  userAccount: "",
  userPassword: "",
  userNewPassword: "",
});

const checkPassword = ref("");

const changePassword = async (values: any) => {
  if (values?.errors) {
    return;
  }
  if (form.userAccount == "") {
    message.error("请输入账号！");
    return;
  }
  if (form.userPassword == "") {
    message.error("请输入旧密码！");
    return;
  }
  if (form.userNewPassword == "") {
    message.error("请输入新密码！");
    return;
  }
  if (form.userNewPassword !== checkPassword.value) {
    message.error("两次输入密码不一致");
    return;
  }
  const res = await UserControllerService.updateUserPasswordUsingPost(form);
  if (res.code === 0) {
    message.success("修改成功");
    router.push({
      path: "/user/login",
    });
  } else {
    message.error("修改失败" + res.message);
  }
};

const toLogin = () => {
  router.push({
    path: "/user/login",
    replace: true,
  });
};
</script>

<style scoped>
.register-cart {
  padding-top: 70px;
  width: 450px;
  background-color: rgba(255, 255, 255, 0.5);
  border-radius: 10%;
  height: 550px;
  position: relative;
  box-shadow: 8px 8px 8px rgba(0, 0, 0, 0.3),
    -8px -8px 8px rgba(255, 255, 255, 0.2);
  transition: all 0.3s;
  color: white;
}

.register-cart:hover {
  transform: translateY(-8px) scale(1.01, 1.01);
  box-shadow: 0px 10px 20px rgba(0, 0, 0, 0.3);
}

.register-cart-content {
  max-width: 350px;
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  margin-left: 2.5rem;
}

.register-cart-header {
  margin-bottom: 50px;
  font-size: 36px;
  font-weight: bold;
  letter-spacing: 2px;
  transition: 1s ease-in-out;
  text-shadow: 2px 2px 4px #000000;
}

.button-submit {
  min-width: 200px;
  margin-left: 15px;
}
</style>
