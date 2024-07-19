<template>
  <div id="userLayout">
    <a-layout style="min-height: 100vh">
      <a-layout-header class="header">
        <a-space>
          <img class="logo" src="../assets/mylogo.jpg" />
          <div class="title">XOJ系统</div>
        </a-space>
      </a-layout-header>
      <a-layout-content class="content">
        <router-view />
      </a-layout-content>
      <a-layout-footer class="footer">
        <a href="##" target="_blank"> XOJ在线判题系统 </a>
      </a-layout-footer>
    </a-layout>
  </div>
  <ul class="bg-squares">
    <li></li>
    <li></li>
    <li></li>
    <li></li>
    <li></li>
    <li></li>
    <li></li>
    <li></li>
    <li></li>
    <li></li>
  </ul>
</template>

<script setup lang="ts"></script>

<style scoped>
#userLayout {
  background-size: cover;
  min-height: 100vh;
  background: linear-gradient(to right, #b4f6ee, #f870b4, #efbc9e);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

#userLayout .header {
  margin-top: 16px;
}

#userLayout .content {
  text-align: center;
  margin-bottom: 16px;
  padding: 20px;
}

#userLayout .footer {
  padding: 16px;
  position: sticky;
  bottom: 0;
  left: 0;
  right: 0;
  text-align: center;
}

.title {
  margin-top: 44px;
  margin-left: 10px;
  font-size: 20px;
  text-shadow: 2px 2px 2px #000000;
  color: #e88808;
}

.logo {
  height: 80px;
  width: 55px;
  margin-left: 28px;
  background-size: initial;
}

/*背景方块  */
.bg-squares {
  list-style: none;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.bg-squares li {
  width: 40px;
  height: 40px;
  background-color: rgba(255, 255, 255, 0.1);
  position: absolute;
  bottom: 160px;
  /* 执行动画： 动画名 时长  线性  无限次循环 */
  animation: square 2s linear infinite;
}

/* 为灭一个方块设置*/
.bg-squares li:nth-child(1) {
  left: 10%;
}

.bg-squares li:nth-child(2) {
  left: 20%;
  width: 80px;
  height: 80px;
  /*动画延迟时间 */
  animation-delay: 2s;
  /*动画时长*/
  animation-duration: 17s;
}

.bg-squares li:nth-child(3) {
  left: 25%;
  animation-delay: 4s;
}

.bg-squares li:nth-child(4) {
  left: 40%;
  width: 60px;
  height: 60px;
  background-color: rgba(255, 255, 255, 0.4);
  /*动画时长*/
  animation-duration: 22s;
}

.bg-squares li:nth-child(5) {
  left: 70%;
}

.bg-squares li:nth-child(6) {
  left: 80%;
  width: 120px;
  height: 120px;
  background-color: rgba(255, 255, 255, 0.2);
  /*动画时长*/
  animation-delay: 4s;
}

.bg-squares li:nth-child(7) {
  left: 32%;
  width: 160px;
  height: 160px;
  /*动画时长*/
  animation-delay: 7s;
}

.bg-squares li:nth-child(8) {
  left: 55%;
  width: 10px;
  height: 10px;
  background-color: rgba(255, 255, 255, 0.3);
  /*动画时长*/
  animation-delay: 15s;
  animation-duration: 22s;
}

.bg-squares li:nth-child(9) {
  left: 25%;
  width: 10px;
  height: 10px;
  background-color: rgba(255, 255, 255, 0.3);
  /*动画时长*/
  animation-delay: 2s;
  animation-duration: 40s;
}

.bg-squares li:nth-child(10) {
  left: 90%;
  width: 160px;
  height: 160px;
  background-color: rgba(255, 255, 255, 0.3);
  /*动画时长*/
  animation-delay: 8s;
  animation-duration: 30s;
}

/* 电仪动画*/
@keyframes square {
  0% {
    transform: translateY(0);
  }
  100% {
    transform: translateY(-120vh) rotate(600deg);
  }
}
</style>
