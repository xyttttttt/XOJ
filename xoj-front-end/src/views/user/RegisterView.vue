<template>
  <div id="userRegister">
    <a-space direction="vertical">
      <div class="register-cart">
        <h1 class="register-cart-header">用户注册</h1>
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
            <a-input v-model="form.userAccount" placeholder="输入你的账号" />
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
          <a-form-item
            field="userCheckedPassword"
            label="确认密码"
            style="margin: 0 auto"
          >
            <a-input-password
              v-model="form.checkPassword"
              placeholder="请确认您的密码"
            />
          </a-form-item>

          <a-form-item
            field="isRead"
            v-model="form.isRead"
            @click="changeReadStatus"
          >
            <a-checkbox> 我已阅读同意协议</a-checkbox>
            <a-link @click="handleClick" style="margin-left: 5px"
              >用户协议
            </a-link>
            <a-modal
              v-model:visible="visible"
              @ok="handleOk"
              @cancel="handleCancel"
              width="830px"
              draggable
            >
              <template #title><h3>用户协议</h3></template>
              <div v-html="UserAgreement"></div>
            </a-modal>
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
              >已有账号，去登陆!</a
            >
          </div>
          <a-form-item>
            <a-button
              type="primary"
              html-type="submit"
              class="button-submit"
              @click="userRegister"
              >注册
            </a-button>
          </a-form-item>
        </a-form>
      </div>
    </a-space>
  </div>
</template>
<script setup lang="ts">
import { ref } from "vue";
import { UserControllerService } from "../../../generated";
import { useRouter } from "vue-router";
import message from "@arco-design/web-vue/es/message";

const router = useRouter();
const form = ref({
  userAccount: "",
  userPassword: "",
  checkPassword: "",
  isRead: false,
});

const changeReadStatus = () => {
  form.value.isRead = !form.value.isRead;
};

const userRegister = async (values: any, errors: any) => {
  if (values?.errors) {
    return;
  }
  if (form.value.userAccount == "") {
    message.error("请输入账号！");
    return;
  }
  if (form.value.userPassword == "") {
    message.error("请输入密码！");
    return;
  }
  if (form.value.userPassword !== form.value.checkPassword) {
    message.error("两次输入密码不一致");
    return;
  }
  if (form.value.isRead === false) {
    message.error("请勾选同意协议");
    return;
  }
  const res = await UserControllerService.userRegisterUsingPost(form.value);
  if (res.code === 0) {
    message.success("注册成功");
    router.push({
      path: "/user/login",
    });
  } else {
    message.error("注册失败" + res.message);
  }
};

const toLogin = () => {
  router.push({
    path: "/user/login",
    replace: true,
  });
};

const visible = ref(false);

const handleClick = () => {
  visible.value = true;
};
const handleOk = () => {
  visible.value = false;
};
const handleCancel = () => {
  visible.value = false;
};

const UserAgreement =
  "<strong>1. 服务概述\n</strong> <br>" +
  '本在线判题系统（以下简称"系统"）旨在为使用者提供一个便捷、高效的在线判题环境。系统提供了一个平台，用户可以上传、提交和查看作业题目，以及查看系统的判题结果。\n <br>' +
  "\n" +
  "<strong>2. 使用条款\n</strong><br>" +
  "<strong>2.1 账户创建\n</strong><br>" +
  "用户在系统上创建账户需要提供真实、准确的信息，并保证这些信息的及时更新。用户需对自己的账户信息安全负责。\n<br>" +
  "\n" +
  "<strong>2.2 提交题目\n</strong><br>" +
  "用户可以在系统上提交需要判题的题目。题目应符合系统的要求和规定，不得包含任何违反法律、道德或学术规范的内容。\n<br>" +
  "\n" +
  "<strong>2.3 判题服务\n</strong><br>" +
  "系统将根据用户提交的题目进行判题，并提供判题结果。用户需自行判断判题结果的准确性。\n<br>" +
  "\n" +
  "<strong>2.4 禁止行为\n</strong><br>" +
  "用户不得使用系统进行任何违反法律、道德或学术规范的行为，包括但不限于上传恶意代码、侵犯他人知识产权、作弊等。\n<br>" +
  "\n" +
  "<strong>3. 隐私保护\n</strong><br>" +
  "系统将收集和存储用户的基本信息（如姓名、邮箱等）以及使用记录。这些信息将用于改进系统服务、进行内部研究和满足法律要求。但未经用户同意，系统不会泄露用户的个人信息。\n<br>" +
  "\n" +
  "<strong>4. 技术支持与维护\n</strong><br>" +
  "系统将尽力保证服务的正常运行。但因不可抗力因素导致的技术问题或服务中断，系统不承担责任。用户应理解并接受这一点。\n<br>" +
  "\n" +
  "<strong>5. 法律条款\n</strong><br>" +
  "本协议受相关法律法规的约束。若发生任何争议，双方应首先协商解决；协商无果的，应提交给相关法律机构解决。\n<br>" +
  "\n" +
  "以上就是我们的用户协议。我们希望用户在使用我们的服务时能够遵守这些规定，共同维护一个健康、公正、公平的在线判题环境。谢谢您的理解和支持！<br>";
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
