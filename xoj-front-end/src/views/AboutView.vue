<template>
  <div class="about">
    <h1>This is an about page</h1>
    <md-editor :value="mdValue" :handle-change="onMdChange" />
    <code-editor :value="codeValue" :handle-change="onCodeChange" />
  </div>
</template>
<script setup lang="ts">
import { ref } from "vue";
import MdEditor from "@/components/MdEditor.vue";
import CodeEditor from "@/components/CodeEditor.vue";

const mdValue = ref();

const onMdChange = (v: string) => {
  mdValue.value = v;
};

const codeValue = ref();

const onCodeChange = (v: string) => {
  codeValue.value = v;
  console.log(v);
};
</script>
