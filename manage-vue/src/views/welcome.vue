<template>
    <div id="welcome">
        <el-breadcrumb separator="/" style="padding-left:10px;padding-bottom:10px;font-size:12px;">
            <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>欢迎</el-breadcrumb-item>
        </el-breadcrumb>
        <el-row :gutter="15">
            <!-- 左边部分 -->
            <!-- <el-col :span="20"> -->
                <!-- 用户信息表格 -->
                <el-card class="box-card">
                    <div slot="header" class="clearfix">
                        <span>用户信息</span>
                    </div>
                    <el-tooltip class="item" effect="dark" content="换头像功能还未实现" placement="top-start">
                        <el-avatar
                                shape="square"
                                :size="80"
                                :src="userInfo.avatar"
                                style="float:left;"
                                :key="1"
                        ></el-avatar>
                    </el-tooltip>
                    <div class="right" style="float:right;width:520px;">
                        <el-table :data="tableInfo" border height="100">
                            <el-table-column prop="username" label="用户账号"></el-table-column>
                            <el-table-column prop="nickname" label="用户昵称"></el-table-column>
                            <el-table-column prop="department" label="所属部门"></el-table-column>
                            <el-table-column fixed="right" prop="roles" label="用户角色" width="150"></el-table-column>
                        </el-table>
                    </div>
                </el-card>
        </el-row>

        <!-- <el-card class="box-card">
               <el-calendar v-model="value"></el-calendar>
        </el-card>-->
    </div>
</template>
<script>
    import echarts from "echarts";
    import aplayer from "vue-aplayer";
    export default {
        components: {
            //别忘了引入组件
            aplayer: aplayer
        },

        data() {
            return {
                xData: [],
                yData: [],
                myData: [],
                value: new Date(),
                userInfo: {},
                tableInfo: [],
                musicList:[{
                    "artist": "Eminem",
                    "lrc": "",
                    "title": "Airplanes",
                    "src": "http://music.163.com/song/media/outer/url?id=26714821.mp3",
                    "pic": "http://p4.music.126.net/H9HJibEzTL34aIT6nsqKsQ==/5682276092402519.jpg"
                },{
                    "artist": "Tinashe",
                    "lrc": "",
                    "title": "Story of Us",
                    "src": "http://music.163.com/song/media/outer/url?id=1403428061.mp3",
                    "pic": "http://p3.music.126.net/l2XttTpEa14IEZtUsQX1HA==/109951164486978461.jpg"
                }, {
                    "artist": "Chris Brown",
                    "lrc": "",
                    "title": "War For You",
                    "src": "http://music.163.com/song/media/outer/url?id=30431534.mp3",
                    "pic": "http://p3.music.126.net/YWkl1JXVKm7bOBAew72lGg==/109951163958771792.jpg"
                }, {
                    "artist": "Sarah Darling",
                    "lrc": "",
                    "title": "Jack of Hearts",
                    "src": "http://music.163.com/song/media/outer/url?id=19132440.mp3",
                    "pic": "http://p4.music.126.net/4q3kpn5VLo3x7hVWttj0QA==/109951164802108652.jpg"
                }, {
                    "artist": "Benjamin Ingrosso",
                    "lrc": "",
                    "title": "Costa Rica",
                    "src": "http://music.163.com/song/media/outer/url?id=1372897252.mp3",
                    "pic": "http://p4.music.126.net/mmm97zC81t73rToPFuXXnw==/109951164159882466.jpg"
                }, {
                    "artist": "Yo Trane",
                    "lrc": "",
                    "title": "Affection",
                    "src": "http://music.163.com/song/media/outer/url?id=1393553542.mp3",
                    "pic": "http://p4.music.126.net/T_vdbfQPO4HE4zVE_8rgCQ==/109951164389023010.jpg"
                }]
            };
        },
        methods: {
            /**
             * 点击获取源码
             */
            getPage(url) {
                const w = window.open("about:blank");
                w.location.href = url;
            },
            /**
             * 加载登入报表数据
             */
            async loginReport(username) {
                const { data: res } = await this.$http.post("system/loginLog/loginReport", {
                    username: username
                });
                if(!res.success){
                    return this.$message.error("获取登入报表数据失败:" + res.msg);
                } else {
                    const $this = this;
                    this.xData = [];
                    this.yData = [];
                    this.myData = [];
                    res.data.all.forEach(e1 => {
                        $this.xData.push(e1.days);
                        $this.yData.push(e1.count);
                    });

                    for (let i = 0; i < this.xData.length; i++) {
                        let count = 0;
                        for (let j = 0; j < res.data.me.length; j++) {
                            if ($this.xData[i] === res.data.me[j].days) {
                                count = res.data.me[j].count;
                                break;
                            } else {
                                count = 0;
                            }
                        }
                        $this.myData.push(count);
                    }
                }
                this.draw();
            },

            /**
             * 绘制登入报表
             */
            draw() {
                const myChart = echarts.init(document.getElementById("loginReport"));
                // 指定图表的配置项和数据
                const option = {
                    title: {
                        text: "用户登入统计"
                    },
                    toolbox: {
                        show: true,
                        feature: {
                            dataZoom: {
                                yAxisIndex: "none"
                            },
                            dataView: {readOnly: false}, //  缩放
                            magicType: {type: ["bar", "line"]}, ///　　折线  直方图切换
                            restore: {}, // 重置
                            saveAsImage: {} // 导出图片
                        }
                    },
                    tooltip: {
                        trigger: 'axis',
                        axisPointer: {
                            type: 'cross',
                            crossStyle: {
                                color: '#eee'
                            }
                        }
                    },
                    legend: {
                        type: 'plain',
                        data: ["全部", "我"]
                    },
                    xAxis: {
                        data: this.xData
                    },
                    yAxis: {
                        type: "value"
                    },
                    series: [
                        {
                            name: "全部",
                            data: this.yData,
                            type: "bar",
                            showBackground: true,
                            animationDuration: 1500,
                            animationEasing: "cubicInOut",
                            itemStyle: {
                                shadowBlur: 10,
                                shadowOffsetX: 0,
                                shadowColor: 'rgba(30, 144, 255，0.5)'
                            },
                        },
                        {
                            name: "我",
                            data: this.myData,
                            type: "bar",
                            showBackground: true,
                            animationDuration: 2000,
                            animationEasing: "cubicInOut"
                        }
                    ]
                };
                // 使用刚指定的配置项和数据显示图表。
                myChart.setOption(option);
            }
        },

        created() {
            this.userInfo = this.$store.state.userInfo;
            const roles = this.userInfo.isAdmin ? "超级管理员" : this.userInfo.roles;
            this.tableInfo.push({
                username: this.userInfo.username,
                nickname: this.userInfo.nickname,
                department: this.userInfo.department,
                roles: roles
            });
        },
        mounted: function() {
            this.loginReport(this.userInfo.username);
            this.draw();
        },

    };
</script>

<style scoped>
    .el-carousel__item h3 {
        color: #475669;
        font-size: 14px;
        opacity: 0.75;
        line-height: 200px;
        margin: 0;
    }

    .el-carousel__item:nth-child(2n) {
        background-color: #99a9bf;
        background-image: url("http://myforum.oss-cn-beijing.aliyuncs.com/postImages/1582867606734617c9668-8086-4c9b-867e-efda0bff78d63.png?Expires=1677475607&OSSAccessKeyId=LTAI4FsV5R1tnt8W8kqFqBYh&Signature=3xKM18EXDDD%2BQmsg%2B0t7uDC%2FMGg%3D");
        background-size: 100% 100%;
    }

    .el-carousel__item:nth-child(2n + 1) {
        background-color: #d3dce6;
        background-image: url("http://myforum.oss-cn-beijing.aliyuncs.com/postImages/15828676585536f809b01-a5c3-4229-8ce6-ed29a7bdaaa22.png?Expires=1677475658&OSSAccessKeyId=LTAI4FsV5R1tnt8W8kqFqBYh&Signature=k2fJfFzwKwp7f2c%2BRD7rdH%2FAj%2Bs%3D");
        background-size: 100% 100%;
    }
</style>
