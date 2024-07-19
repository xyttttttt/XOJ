<template>
  <div id="userProcess">
    <h1>进度管理</h1>
    <h3>提交结果汇总</h3>
    <a-space size="large" max-height="20xp" class="process-control">
      <div id="myChartMain" style="width: 900px; height: 500px"></div>
    </a-space>
  </div>
</template>
<script setup lang="ts">
import { QuestionControllerService } from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import * as echarts from "echarts";
import { onMounted } from "vue";

let submitTotal = "";
let submitAcceptTotal = "";
let questionSubmitAcceptTotal = "";
let questionSubmitTotal = "";

const loadData4 = async () => {
  const res = await QuestionControllerService.userQuestionSubmitInfoUsePost();
  if (res.code === 0) {
    console.log("进度管理", res.data);
    submitTotal = res.data.submitTotal;
    submitAcceptTotal = res.data.submitAcceptTotal;
    questionSubmitAcceptTotal = res.data.questionSubmitAcceptTotal;
    questionSubmitTotal = res.data.questionSubmitTotal;
  } else {
    message.error("加载失败" + res.message);
  }
};

const initEcharts = () => {
  let myChart = echarts.init(
    document.getElementById("myChartMain") as HTMLDivElement
  );
  // 指定图表的配置项和数据
  myChart.setOption({
    backgroundColor: {
      type: "radial",
      x: 0.3,
      y: 0.3,
      r: 0.8,
      colorStops: [
        {
          offset: 0,
          color: "#f7f8fa",
        },
        {
          offset: 1,
          color: "#cdd0d5",
        },
      ],
    },
    tooltip: {
      trigger: "axis",
    },
    //calculable: true,
    xAxis: {
      data: ["提交总数", "提交通过总数", "题目提交总数", "题目通过总数"],
    },
    yAxis: {},
    series: [
      {
        name: "数量",
        type: "bar",
        data: [
          submitTotal,
          submitAcceptTotal,
          {
            value: questionSubmitTotal,
            // 设置单个柱子的样式
            itemStyle: {
              color: "#e69d87",
              opacity: 0.5,
            },
          },
          questionSubmitAcceptTotal,
        ],
        // itemStyle: {
        //   barBorderRadius: 5,
        //   borderWidth: 0.5,
        //   borderType: "solid",
        //   borderColor: "#0eccee",
        //   shadowColor: "#78e5db",
        //   color: "#1f8cd0",
        //   shadowBlur: 3,
        // },
        color: ["#8dc1a2"],
        barWidth: "50%",
      },
    ],
  });
};
onMounted(() => {
  loadData4();
});

onMounted(() => {
  setTimeout(() => {
    initEcharts();
  }, 300);
});
</script>

<style scoped>
.process-control {
  margin-left: 80px;
}
</style>
