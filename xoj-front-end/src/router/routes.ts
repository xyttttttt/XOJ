import { RouteRecordRaw } from "vue-router";
import NoAuthView from "@/views/NoAuthView.vue";
import ACCESS_ENUM from "@/access/accessEnum";
import RegisterView from "@/views/user/RegisterView.vue";
import LoginView from "@/views/user/LoginView.vue";
import UserLayouts from "@/layouts/UserLayouts.vue";
import AddQuestionView from "@/views/question/AddQuestionView.vue";
import ManageQuestionView from "@/views/question/ManageQuestionView.vue";
import QuestionsView from "@/views/question/QuestionsView.vue";
import ViewQuestionsView from "@/views/question/ViewQuestionsView.vue";
import QuestionsSubmitView from "@/views/question/QuestionsSubmitView.vue";
import UserCenterView from "@/views/user/UserCenterView.vue";
import CurrentLimitingView from "@/views/CurrentLimitingView.vue";
import ManageUserView from "@/views/user/ManageUserView.vue";
import ChangePasswordView from "@/views/user/ChangePasswordView.vue";

export const routes: Array<RouteRecordRaw> = [
  {
    path: "/user",
    name: "用户",
    component: UserLayouts,
    children: [
      {
        path: "/user/login",
        name: "用户登录",
        component: LoginView,
      },
      {
        path: "/user/register",
        name: "用户注册",
        component: RegisterView,
      },
      {
        path: "/user/change/password",
        name: "修改密码",
        component: ChangePasswordView,
      },
    ],
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/",
    name: "浏览题目",
    component: QuestionsView,
  },
  /*  {
      path: "/",
      name: "浏览题目",
      component: QuestionsView,
    },*/
  {
    path: "/questions_submit",
    name: "浏览提交题目",
    component: QuestionsSubmitView,
  },
  {
    path: "/add/question",
    name: "创建题目",
    component: AddQuestionView,
    meta: {
      access: ACCESS_ENUM.USER,
    },
  },
  {
    path: "/view/question/:id", //开启props为true 在组件中通过熟悉动态获取id
    name: "在线做题",
    component: ViewQuestionsView,
    props: true,
    meta: {
      access: ACCESS_ENUM.USER,
      hideInMenu: true,
    },
  },
  {
    path: "/update/question",
    name: "更新题目",
    component: AddQuestionView,
    meta: {
      access: ACCESS_ENUM.USER,
      hideInMenu: true,
    },
  },
  {
    path: "/manage/question",
    name: "管理题目",
    component: ManageQuestionView,
    meta: {
      access: ACCESS_ENUM.ADMIN,
    },
  },
  {
    path: "/centerUser",
    name: "个人中心",
    component: UserCenterView,
    meta: {
      access: ACCESS_ENUM.USER,
      hideInMenu: true,
    },
  },
  /*  {
      path: "/manage/user",
      name: "管理用户",
      component: ManageUserView,
      meta: {
        access: ACCESS_ENUM.ADMIN,
      },
    },*/
  /*{
    path: "/hide",
    name: "隐藏模块",
    component: HomeView,
    meta: {
      hideInMenu: true,
    },
  },*/
  {
    path: "/noAuth",
    name: "无权限",
    component: NoAuthView,
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/current/limiting",
    name: "无权限",
    component: CurrentLimitingView,
    meta: {
      hideInMenu: true,
    },
  },
  /*{
    path: "/admin",
    name: "管理员可见",
    component: AdminView,
    meta: {
      access: ACCESS_ENUM.ADMIN,
    },
  },
  {
    path: "/about",
    name: "获取我的",
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () =>
      import(/!* webpackChunkName: "about" *!/ "../views/AboutView.vue"),
  },*/
];
