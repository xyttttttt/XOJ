<template>
  <div>
    <template v-if="route.path.startsWith('/user')">
      <router-view />
    </template>
    <template v-else-if="route.path.startsWith('/noAuth')">
      <NoAuth />
    </template>
    <template v-else>
      <basic-layouts />
    </template>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from "vue";
import BasicLayouts from "@/layouts/BasicLayouts.vue";
import { useRoute } from "vue-router";
import NoAuth from "@/views/NoAuthView.vue";

const route = useRoute();
const doInit = () => {
  console.log("xx");
};

onMounted(() => {
  doInit();
});
</script>
