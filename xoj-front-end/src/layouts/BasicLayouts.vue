<template>
  <div id="basicLayout">
    <a-layout style="min-height: 100vh">
      <div class="shadowTop"></div>
      <a-layout-header class="header">
        <global-header />
      </a-layout-header>
      <a-layout-content class="content">
        <router-view />
      </a-layout-content>
      <a-layout-footer class="footer">
        <a-link href="##" :hoverable="true" class="footerFont">
          XOJ在线判题系统
        </a-link>
      </a-layout-footer>
    </a-layout>
    <div class="shadowBottom"></div>
  </div>
</template>

<script setup lang="ts">
import GlobalHeader from "@/components/GlobalHeader.vue";
</script>

<style scoped>
#basicLayout .header {
  margin-bottom: 16px;
  box-shadow: #eeee 1px 1px 5px;
  background: #ffffff;
}

#basicLayout .content {
  margin-bottom: 16px;
  padding: 20px;
}

#basicLayout .footer {
  background-color: #efefef;
  padding: 16px;
  position: sticky;
  text-align: center;
}

.footerFont {
  font-size: 17px;
  color: coral;
  text-decoration: none;
}

.footerFont:hover {
  color: pink;
}

@property --xPoint {
  syntax: "<length>";
  inherits: false;
  initial-value: 400px;
}
@property --yPoint {
  syntax: "<length>";
  inherits: false;
  initial-value: 300px;
}
@property --x2Point {
  syntax: "<length>";
  inherits: false;
  initial-value: 500px;
}
@property --y2Point {
  syntax: "<length>";
  inherits: false;
  initial-value: 300px;
}

#basicLayout {
  position: relative;
  margin: auto;
  width: 100%;
  height: 100%;
  background: conic-gradient(
      from -45deg at var(--xPoint) var(--yPoint),
      hsla(170deg, 100%, 70%, 0.7),
      transparent 50%,
      hsla(219deg, 90%, 80%, 0.5) 100%
    ),
    conic-gradient(
      from 180deg at var(--x2Point) var(--y2Point),
      hsla(170deg, 100%, 70%, 0.7),
      transparent 50%,
      hsla(219deg, 90%, 80%, 0.5) 100%
    ),
    linear-gradient(-45deg, #06339f, #ee6eae);
  animation: pointMove 2.5s infinite alternate linear;
  overflow: hidden;
}

.shadowTop {
  position: absolute;
  top: -300px;
  left: -330px;
  width: 430px;
  height: 300px;
  background: #fff;
  transform-origin: 100% 100%;
  transform: rotate(225deg);
  clip-path: polygon(-100% 100%, 200% 100%, 200% 500%, -100% 500%);
  box-shadow: 0px 0 0.5px hsla(170deg, 95%, 80%, 1),
    0px 0 1px hsla(170deg, 91%, 80%, 0.95),
    0px 0 2px hsla(171deg, 91%, 80%, 0.95),
    0px 0 3px hsla(171deg, 91%, 80%, 0.95),
    0px 0 4px hsla(171deg, 91%, 82%, 0.9), 0px 0 5px hsla(172deg, 91%, 82%, 0.9),
    0px 0 10px hsla(173deg, 91%, 84%, 0.9),
    0px 0 20px hsla(174deg, 91%, 86%, 0.85),
    0px 0 40px hsla(175deg, 91%, 86%, 0.85),
    0px 0 60px hsla(175deg, 91%, 86%, 0.85);
  animation: scale 2.5s infinite alternate linear;
  mix-blend-mode: hard-light;
}

.shadowBottom {
  position: absolute;
  top: 300px;
  left: 500px;
  width: 400px;
  height: 300px;
  background: #000;
  transform-origin: 0 100%;
  clip-path: polygon(0 -100%, -200% -100%, -200% 200%, 0 200%);
  box-shadow: 0px 0 0.5px hsla(170deg, 95%, 80%, 1),
    0px 0 1px hsla(170deg, 91%, 80%, 0.95),
    0px 0 2px hsla(171deg, 91%, 80%, 0.95),
    0px 0 3px hsla(171deg, 91%, 80%, 0.95),
    0px 0 4px hsla(171deg, 91%, 82%, 0.9), 0px 0 5px hsla(172deg, 91%, 82%, 0.9),
    0px 0 10px hsla(173deg, 91%, 84%, 0.9),
    0px 0 20px hsla(174deg, 91%, 86%, 0.85),
    0px 0 40px hsla(175deg, 91%, 86%, 0.85),
    0px 0 60px hsla(175deg, 91%, 86%, 0.85);
  animation: scaleBottom 2.5s infinite -2.5s alternate linear;
  mix-blend-mode: hard-light;
}

@keyframes scale {
  50%,
  100% {
    transform: rotate(225deg) scale(0);
  }
}

@keyframes scaleBottom {
  50%,
  100% {
    transform: scale(0);
  }
}

@keyframes pointMove {
  0% {
    --x2Point: 500px;
    --y2Point: 600px;
  }
  50% {
    --xPoint: 100px;
    --yPoint: 0;
    --x2Point: 500px;
    --y2Point: 600px;
  }
  100% {
    --xPoint: 100px;
    --yPoint: 0;
    --x2Point: 500px;
    --y2Point: 300px;
  }
}
</style>
