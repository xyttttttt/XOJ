<template>
  <a-result status="403" subtitle="你没有权限访问此页面">
    <template #extra>
      <a-space>
        <a-button type="primary" @click="goBack">返回</a-button>
      </a-space>
    </template>
  </a-result>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";

const router = useRouter();
const goBack = () => {
  router.go(-1);
};
</script>
