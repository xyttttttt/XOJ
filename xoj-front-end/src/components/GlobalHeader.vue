<template>
  <div>
    <a-row id="globalHeader" align="center" :wrap="false">
      <a-col flex="auto">
        <a-menu
          mode="horizontal"
          :selected-keys="selectKeys"
          @menu-item-click="doMenuClick"
        >
          <a-menu-item
            key="0"
            :style="{ padding: 0, marginRight: '38px' }"
            disabled
          >
            <div id="title-bar">
              <img class="logo" src="../assets/mylogo.jpg" />
              <div class="title"><p>XOJ系统</p></div>
            </div>
          </a-menu-item>
          <a-menu-item v-for="item in visibleRoutes" :key="item.path"
            >{{ item.name }}
          </a-menu-item>
        </a-menu>
      </a-col>
      <a-col flex="20px" class="login">
        <div
          style="inset-inline: auto; display: inline-flex"
          v-if="!store.state.user.loginUser.userName"
        >
          <a-space>
            <a-link @click="toLogin">登录</a-link>
            <a-link @click="toRegister">注册</a-link>
          </a-space>
        </div>
        <div v-if="store.state.user.loginUser.userName">
          <a-avatar>
            <img :src="store.state.user.loginUser.userAvatar" />
          </a-avatar>
          <a-dropdown-button style="padding-left: 10px">
            {{ store.state.user.loginUser.userName }}
            <template #icon>
              <icon-down style="background-color: #4ba4da" />
            </template>
            <template #content>
              <a-doption @click="toUserCenter(id as string)"
                >个人中心
              </a-doption>
              <a-doption @click="userLogout">注销</a-doption>
            </template>
          </a-dropdown-button>
        </div>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { routes } from "@/router/routes";
import { useRouter } from "vue-router";
import { computed, ref } from "vue";
import { useStore } from "vuex";
import checkAccess from "@/access/checkAccess";
import { UserControllerService } from "../../generated";
import message from "@arco-design/web-vue/es/message";
import cookie from "js-cookie";

const router = useRouter();
const store = useStore();
//展示在菜单中的路由数组
const visibleRoutes = computed(() => {
  return routes.filter((item, index) => {
    if (item.meta?.hideInMenu) {
      return false;
    }
    if (!checkAccess(store.state.user.loginUser, item?.meta?.access as string))
      return false;
    return true;
  });
});
let url = ref("");
const id = store.state.user.loginUser.id;

const userLogout = async () => {
  const res = await UserControllerService.userLogoutUsingPost();
  if (res.code === 0) {
    store.state.user.loginUser.userName = "";
    localStorage.removeItem("token");
    router.go(0);
  } else {
    message.error("退出失败，系统异常" + res.message);
  }
};

const toLogin = () => {
  router.push({
    path: "/user/login",
  });
};
const toUserCenter = (id: string) => {
  router.push({
    path: "/centerUser",
  });
};
const toRegister = () => {
  router.push({
    path: "/user/register",
  });
};

const doMenuClick = (key: string) => {
  router.push({
    path: key,
  });
};

const selectKeys = ref(["/"]);

//路由跳转时 更新选中的路由
router.afterEach((to, next, error) => {
  selectKeys.value = [to.path];
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
#title-bar {
  display: flex;
  align-items: center;
}

.logo {
  height: 80px;
  width: 55px;
  background-size: initial;
}

.login {
  color: cornflowerblue;
  font-size: 16px;
  white-space: nowrap;
  padding: 20px;
}

#globalHeader {
}

.title {
  margin-top: 44px;
  margin-left: 10px;
  font-size: 20px;
  text-shadow: 2px 2px 2px #000000;
  color: #e88808;
}
</style>
