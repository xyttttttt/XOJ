<template>
  <div id="userLogin">
    <a-space direction="vertical">
      <div class="login-cart">
        <h1 class="login-cart-header">用户登录</h1>
        <a-form
          class="login-cart-content"
          label-align="left"
          :model="form"
          @submit="handleSubmit"
        >
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
              placeholder="请输入账号"
              allow-clear
            />
            <template #prefix>
              <icon-user />
            </template>
          </a-form-item>
          <a-form-item
            field="userPassword"
            label="密码"
            :validate-trigger="['blur']"
            :rules="[
              { minLength: 6, message: '密码长度不能少于6位' },
              { maxLength: 16, message: '密码长度不能超过16位' },
            ]"
          >
            <a-input-password
              v-model="form.userPassword"
              placeholder="请输入密码"
            />
          </a-form-item>
          <a-form-item field="autoLogin" style="text-aligt: center">
            <div style="text-align: left">
              <a
                style="cursor: pointer; color: #1677ff"
                @click="ToChangePassword"
                >修改密码</a
              >
            </div>
            <div style="width: 70%; text-align: right">
              <a style="cursor: pointer; color: #1677ff" @click="ToRegister"
                >没有账号？去注册</a
              >
            </div>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" html-type="submit" class="loginButton"
              >登录
            </a-button>
          </a-form-item>
        </a-form>
      </div>
    </a-space>
  </div>
</template>
<script setup lang="ts">
import { reactive } from "vue";
import { UserControllerService, UserLoginRequest } from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";
import { useStore } from "vuex";
import cookie from "js-cookie";

const form = reactive({
  userAccount: "",
  userPassword: "",
} as UserLoginRequest);

const router = useRouter();
const store = useStore();

const handleSubmit = async (values: any, errors: any) => {
  if (values?.errors) {
    return;
  }
  if (form.userAccount == "") {
    message.error("请输入账号！");
    return;
  }
  if (form.userPassword == "") {
    message.error("请输入密码！");
    return;
  }
  const res = await UserControllerService.userLoginUsingPost(form);
  //登录成功返回主页
  if (res.code === 0) {
    await store.dispatch("user/getLoginUser");
    localStorage.setItem("token", res.data.token);
    router.push({
      path: "/",
      replace: true,
    });
  } else {
    message.error("登录失败" + res.message);
  }
};

// 去注册页面
const ToRegister = () => {
  router.push({
    path: "/user/register",
    replace: true,
  });
};

const ToChangePassword = () => {
  router.push({
    path: "/user/change/password",
  });
};
</script>
<style scoped>
#userLogin {
  text-align: center;
  color: white;
}

.login-cart {
  padding-top: 70px;
  width: 450px;
  background-color: rgba(255, 255, 255, 0.5);
  border-radius: 10%;
  height: 550px;
  position: relative;
  box-shadow: 8px 8px 8px rgba(0, 0, 0, 0.3),
    -8px -8px 8px rgba(255, 255, 255, 0.2);
  transition: all 0.3s;
}

.login-cart:hover {
  transform: translateY(-8px) scale(1.01, 1.01);
  box-shadow: 0px 10px 20px rgba(0, 0, 0, 0.3);
}

/*.login-cart::before {
  content: "";
  position: absolute;
  background-color: rgba(255, 255, 255, 0.6);
  inset: 0;
  transform: rotate(-5deg);
  z-index: -1;
}*/

.login-cart-header {
  margin-bottom: 50px;
  font-size: 36px;
  font-weight: bold;
  letter-spacing: 2px;
  transition: 1s ease-in-out;
  text-shadow: 2px 2px 4px #000000;
}

.login-cart-header,
.login-cart-content {
  max-width: 350px;
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  margin-left: 3.3rem;
  transition: opacity 0.5s;
}

.loginButton {
  text-align: center;
  min-width: 200px;
  outline: none;
  border-radius: 3px;
  cursor: pointer;
  transition: 0.5s;
}
</style>
