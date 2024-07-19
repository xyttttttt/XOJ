<template>
  <div
    id="code-editor"
    ref="codeEditorRef"
    style="min-height: 400px; height: 65vh; margin-top: 17px"
  ></div>
</template>
<script setup lang="ts">
import * as monaco from "monaco-editor";
import { defineProps, onMounted, ref, toRaw, watch, withDefaults } from "vue";

interface Props {
  value: string;
  language?: string;
  handleChange: (v: string) => void;
}

const props = withDefaults(defineProps<Props>(), {
  value: () => "",
  language: () => "java",
  handleChange: (v: string) => {
    console.log(v);
  },
});

const codeEditorRef = ref();
const codeEditor = ref();
const value = ref("hello world");

watch(
  () => props.language,
  () => {
    if (codeEditor.value) {
      monaco.editor.setModelLanguage(
        toRaw(codeEditor.value).getModel(),
        props.language
      );
    }
  }
);

onMounted(() => {
  if (!codeEditorRef.value) return;
  codeEditor.value = monaco.editor.create(codeEditorRef.value, {
    value: props.value,
    language: props.language,
    lineNumbers: "on",
    automaticLayout: true,
    colorDecorators: true,
    readOnly: false,
    theme: "CodeSampleTheme",
    minimap: {
      enabled: true,
    },
    renderLineHighlight: "all",
    roundedSelection: true,
    cursorSurroundingLines: 0,
    cursorBlinking: "smooth",
    folding: true,
  });

  //编辑 监听内容变化
  codeEditor.value.onDidChangeModelContent(() => {
    //console.log("目前内容为", toRaw(codeEditor.value).getValue());
    props.handleChange(toRaw(codeEditor.value).getValue());
  });

  // 自定义主题背景色
  monaco.editor.defineTheme("CodeSampleTheme", {
    base: "vs",
    inherit: true,
    rules: [{ background: "#eafcff" }],
    colors: {
      // 相关颜色属性配置
      "editor.background": "#eafcff", // 背景色
    },
  });
});
</script>
<style scoped></style>
