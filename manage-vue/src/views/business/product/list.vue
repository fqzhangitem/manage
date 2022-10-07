<template>
  <div id="productCategroys">
    <!-- 面包导航 -->
    <el-breadcrumb separator="/" style="padding-left:10px;padding-bottom:10px;font-size:12px;">
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>设备管理</el-breadcrumb-item>
      <el-breadcrumb-item>设备催交</el-breadcrumb-item>
    </el-breadcrumb>
    <!-- 右侧卡片区域 -->
    <el-card class="box-card">
      <el-container style="margin-bottom:20px;">
        <el-alert
            show-icon
            title="友情提示:  设备的类别只支持三级类别"
            type="warning">
        </el-alert>
      </el-container>
      <el-row :gutter="6">
        <el-col :span="4">
          <el-input
              clearable
              size="small"
              v-model="queryMap.name"
              placeholder="请输入设备名称"
              @clear="search"
              class="input-with-select"
          ></el-input>
        </el-col>
        <el-col :span="3">
          <el-cascader
              size="small"
              placeholder="选择设备类型"
              :change-on-select="true"
              @change="selectChange"
              v-model="categorykeys"
              :props="searchSelectProps"
              :options="cateories"
              clearable
          ></el-cascader>
        </el-col>
        <el-col :span="3">
          <el-input
              clearable
              size="small"
              v-model="queryMap.head"
              placeholder="催交负责人"
              @clear="search"
              class="input-with-select"
          ></el-input>
        </el-col>
        <el-col :span="2">
          <template>
            <el-select size="small" v-model="queryMap.status" @click="search" placeholder="请选择状态">
              <el-option label="全部" :value="0"></el-option>
              <el-option label="一个月内" :value="5"></el-option>
              <el-option label="三个月内" :value="3"></el-option>
              <el-option label="半年内" :value="4"></el-option>
              <el-option label="已到期" :value="2"></el-option>
            </el-select>
          </template>
        </el-col>
        <el-col :span="12">
          <el-button size="small" type="primary" icon="el-icon-search" @click="search">查找</el-button>
          <el-button size="small" type="warning" icon="el-icon-search" @click="findByStatus(5)">一个月内</el-button>
          <el-button size="small" type="info" icon="el-icon-search" @click="findByStatus(3)">三个月内</el-button>
          <el-button size="small" type="primary" icon="el-icon-search" @click="findByStatus(4)">半年内</el-button>
          <el-button size="small" type="success" icon="el-icon-circle-plus-outline" @click="openAdd"
                     v-hasPermission="'product:add'">添加
          </el-button>
          <el-button size="small" icon="el-icon-refresh-right" @click="resetForm">重置</el-button>
        </el-col>
      </el-row>

      <!-- 表格区域 -->
      <template>
        <el-table
            size="mini"
            v-loading="loading"
            border
            stripe
            :data="productData"
            :header-cell-style="{textAlign: 'center'}"
            :cell-style="{ textAlign: 'center' }"
            style="width: 100%;margin-top:20px;"
            height="400"
            @sort-change='findBySort'
        >
          <el-table-column prop="id" type="index" label="序号"></el-table-column>
          <el-table-column prop="name" label="设备名称" width="100" :show-overflow-tooltip="true">
          </el-table-column>
          <el-table-column prop="model" label="设备类别" width="100" :show-overflow-tooltip="true">
          </el-table-column>
          <el-table-column prop="unit" label="总包" width="80" :show-overflow-tooltip="true">
          </el-table-column>
          <el-table-column prop="remark" label="分包" width="100" :show-overflow-tooltip="true">
          </el-table-column>
          <el-table-column prop="head" label="催交负责人" width="100" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column prop="mobilePhone" label="负责人联系方式" width="120"
                           :show-overflow-tooltip="true"></el-table-column>
          <el-table-column prop="plan" label="设备进度" width="80" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column prop="arrivalTime" sortable='custom' label="到货时间" width="100"></el-table-column>
          <el-table-column prop="createTime" sortable='custom' label="创建时间" width="100"></el-table-column>
          <el-table-column prop="status" label="状态" width="80">
            <template slot-scope="scope">
              <el-tag size="mini" type="info" effect="plain" v-if="scope.row.status==3">三月内</el-tag>
              <el-tag size="mini" type="success" effect="plain" v-if="scope.row.status==0">正常</el-tag>
              <el-tag size="mini" effect="warning" v-if="scope.row.status==5">一月内</el-tag>
              <el-tag size="mini" effect="primary" v-if="scope.row.status==4">半年内</el-tag>
              <el-tag size="mini" effect="danger" v-if="scope.row.status==2">已到期</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template slot-scope="scope">
              <el-button type="text" size="mini" icon="el-icon-edit" @click="edit(scope.row.id)">编辑</el-button>
              <el-button type="text" size="mini" icon="el-icon-tickets" @click="find(scope.row.id)">查看</el-button>
              <el-button style="margin-left:10px;" type="text" size="mini" icon="el-icon-delete"
                         @click="del(scope.row.id)">删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </template>
      <!-- 分页 -->
      <el-pagination
          style="margin-top:10px;"
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="this.queryMap.pageNum"
          :page-sizes="[6, 10, 15, 20]"
          :page-size="this.queryMap.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
      ></el-pagination>
      <!-- 设备添加弹出框 -->
      <el-dialog title="添加设备" :visible.sync="addDialogVisible" width="50%" @close="closeAddDialog">
        <span>
          <el-form
              size="mini"
              :model="addRuleForm"
              :rules="addRules"
              ref="addRuleFormRef"
              label-width="100px"
              class="demo-ruleForm"
          >
             <el-row>
                <el-col :span="12">
                  <div class="grid-content bg-purple">
                    <el-form-item label="设备名称" prop="name">
                      <el-input v-model="addRuleForm.name"></el-input>
                    </el-form-item>
                  </div>
                </el-col>
                <el-col :span="12">
                  <div class="grid-content bg-purple">
                    <el-form-item label="类别" prop="categoryKeys">
                      <el-cascader
                          :options="cateories"
                          clearable
                          filterable
                          :props="selectProps"
                          v-model="addRuleForm.categoryKeys"
                      ></el-cascader>
                    </el-form-item>
                  </div>
                </el-col>
             </el-row>
              <el-row>
                <el-col :span="12">
                  <div class="grid-content bg-purple">
                    <el-form-item label="总包" prop="unit">
                      <el-input v-model="addRuleForm.unit"></el-input>
                    </el-form-item>
                  </div>
                </el-col>
                <el-col :span="12">
                   <div class="grid-content bg-purple">
                      <el-form-item label="分包" prop="remark">
                        <el-input v-model="addRuleForm.remark"></el-input>
                      </el-form-item>
                    </div>
                 </el-col>
              </el-row>
              <el-row>
                <el-col :span="12">
                  <div class="grid-content bg-purple">
                      <el-form-item label="催交负责人" prop="head">
                        <el-input v-model="addRuleForm.head"></el-input>
                      </el-form-item>
                  </div>
                </el-col>
                <el-col :span="12">
                      <div class="grid-content bg-purple">
                          <el-form-item label="联系方式" prop="mobilePhone">
                            <el-input placeholder="负责人联系方式" v-model="addRuleForm.mobilePhone"></el-input>
                          </el-form-item>
                      </div>
                </el-col>
              </el-row>
             <el-row>
                <el-col :span="12">
                      <div class="grid-content bg-purple">
                          <el-form-item label="生产号令" prop="productionOrder">
                            <el-input v-model="addRuleForm.productionOrder"></el-input>
                          </el-form-item>
                      </div>
                </el-col>
               <el-col :span="12">
                      <div class="grid-content bg-purple">
                          <el-form-item label="发货地点" prop="deliveryAddress">
                            <el-input v-model="addRuleForm.deliveryAddress"></el-input>
                          </el-form-item>
                      </div>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="12">
                      <div class="grid-content bg-purple">
                          <el-form-item label="制造厂家" prop="manufacturer">
                            <el-input v-model="addRuleForm.manufacturer"></el-input>
                          </el-form-item>
                      </div>
                </el-col>
               <el-col :span="12">
                      <div class="grid-content bg-purple">
                          <el-form-item label="制造环节" prop="manufacturing">
                            <el-input v-model="addRuleForm.manufacturing"></el-input>
                          </el-form-item>
                      </div>
                </el-col>
              </el-row>
            <el-row>
               <el-col :span="12">
                      <div class="grid-content bg-purple">
                          <el-form-item label="设备进度" prop="plan">
                            <el-input v-model="addRuleForm.plan"></el-input>
                          </el-form-item>
                      </div>
                </el-col>
               <el-col :span="12">
                  <div class="grid-content bg-purple">
                    <template>
                      <el-form-item label="生产日期" prop="productionTime">
                      <el-date-picker
                          v-model="addRuleForm.productionTime"
                          type="date"
                          placeholder="进入生产工序时间"
                          value-format="yyyy-MM-dd"
                          @change="changedate"
                      >
                      </el-date-picker>
                         </el-form-item>
                    </template>
                  </div>
                </el-col>
              </el-row>

            <el-row>
               <el-col :span="12">
                  <div class="grid-content bg-purple">
                      <el-form-item label="运输周期" prop="period">
                        <el-input v-model="addRuleForm.period"></el-input>
                      </el-form-item>
                  </div>
               </el-col>
               <el-col :span="12">
                <div class="grid-content bg-purple">
                  <template>
                    <el-form-item label="完工日期" prop="completionTime">
                    <el-date-picker
                        v-model="addRuleForm.completionTime"
                        type="date"
                        placeholder="预计完工日期"
                        value-format="yyyy-MM-dd"
                        @change="changedate"
                    >
                    </el-date-picker>
                       </el-form-item>
                  </template>
                </div>
              </el-col>
              </el-row>
            <el-row>
              <el-col :span="12">
                  <div class="grid-content bg-purple">
                      <el-form-item label="是否进口物资" prop="importGoods">
                        <el-input placeholder="是否包含进口物资" v-model="addRuleForm.importGoods"></el-input>
                      </el-form-item>
                  </div>
               </el-col>
              <el-col :span="12">
                  <div class="grid-content bg-purple">
                    <template>
                      <el-form-item label="航运日期" prop="shippingTime">
                      <el-date-picker
                          v-model="addRuleForm.shippingTime"
                          type="date"
                          placeholder="进口物资航运时间"
                          value-format="yyyy-MM-dd"
                          @change="changedate"
                      >
                      </el-date-picker>
                         </el-form-item>
                    </template>
                  </div>
                </el-col>
            </el-row>
              <el-row>
                <el-col :span="12">
                  <div class="grid-content bg-purple">
                    <template>
                      <el-form-item label="发货日期" prop="arrivalTime">
                      <el-date-picker
                          v-model="addRuleForm.arrivalTime"
                          type="date"
                          placeholder="开始发货日期"
                          value-format="yyyy-MM-dd"
                          @change="changedate"
                      >
                      </el-date-picker>
                         </el-form-item>
                    </template>
                  </div>
                </el-col>
                <el-col :span="12">
                  <div class="grid-content bg-purple">
                    <template>
                      <el-form-item label="预计日期" prop="expectTime">
                      <el-date-picker
                          v-model="addRuleForm.expectTime"
                          type="date"
                          placeholder="预计到货日期"
                          value-format="yyyy-MM-dd"
                          @change="changedate"
                      >
                      </el-date-picker>
                         </el-form-item>
                    </template>
                  </div>
                </el-col>
              </el-row>
             <el-row>
               <el-col :span="12">
                  <div class="grid-content bg-purple">
                    <template>
                      <el-form-item label="实际日期" prop="actualTime">
                      <el-date-picker
                          v-model="addRuleForm.actualTime"
                          type="date"
                          placeholder="实际到货日期"
                          value-format="yyyy-MM-dd"
                          @change="changedate"
                      >
                      </el-date-picker>
                         </el-form-item>
                    </template>
                  </div>
                </el-col>
               <el-col :span="12">
                  <div class="grid-content bg-purple">
                      <el-form-item label="满足需求" prop="meetDemand">
                        <el-input  placeholder="是否满足到货需求" v-model="addRuleForm.meetDemand"></el-input>
                      </el-form-item>
                  </div>
                </el-col>
              </el-row>
            <el-row>
               <el-col :span="12">
                  <div class="grid-content bg-purple">
                      <el-form-item label="未到货原因" prop="goodsReason">
                        <el-input v-model="addRuleForm.goodsReason"></el-input>
                      </el-form-item>
                  </div>
                </el-col>
              <el-col :span="12">
                  <div class="grid-content bg-purple">
                      <el-form-item label="目前状态" prop="currentState">
                        <el-input v-model="addRuleForm.currentState"></el-input>
                      </el-form-item>
                  </div>
                </el-col>
            </el-row>
            <el-row>
               <el-col :span="12">
                      <div class="grid-content bg-purple">
                          <el-form-item label="图纸变更" prop="drawingChanges">
                            <el-input placeholder="图纸设计是否变更" v-model="addRuleForm.drawingChanges"></el-input>
                          </el-form-item>
                      </div>
                </el-col>
              <el-col :span="12">
                <div class="grid-content bg-purple">
                    <el-form-item label="变更原因" prop="changesReason">
                      <el-input v-model="addRuleForm.changesReason"></el-input>
                    </el-form-item>
                </div>
                </el-col>
            </el-row>
            <el-row>
               <el-col :span="12">
                  <div class="grid-content bg-purple">
                      <el-form-item label="变更进度" prop="drawingReview">
                        <el-input placeholder="图纸变更进度" v-model="addRuleForm.drawingReview"></el-input>
                      </el-form-item>
                  </div>
                </el-col>
              <el-col :span="12">
                  <div class="grid-content bg-purple">
                    <template>
                      <el-form-item label="收到日期" prop="expectedTime">
                      <el-date-picker
                          v-model="addRuleForm.actualTime"
                          type="date"
                          placeholder="预计图纸收到日期"
                          value-format="yyyy-MM-dd"
                          @change="changedate"
                      >
                      </el-date-picker>
                         </el-form-item>
                    </template>
                  </div>
                </el-col>
              </el-row>
            <el-row>
               <el-col :span="12">
                      <div class="grid-content bg-purple">
                    <template>
                      <el-form-item label="协议签署日期" prop="agreementTime">
                      <el-date-picker
                          v-model="addRuleForm.agreementTime"
                          type="date"
                          placeholder="技术协议签署日期"
                          value-format="yyyy-MM-dd"
                          @change="changedate"
                      >
                      </el-date-picker>
                         </el-form-item>
                    </template>
                  </div>
                </el-col>
              <el-col :span="12">
                      <div class="grid-content bg-purple">
                    <template>
                      <el-form-item label="合同签署日期" prop="signingTime">
                      <el-date-picker
                          v-model="addRuleForm.signingTime"
                          type="date"
                          placeholder="合同签署日期"
                          value-format="yyyy-MM-dd"
                          @change="changedate"
                      >
                      </el-date-picker>
                         </el-form-item>
                    </template>
                  </div>
                </el-col>
              </el-row>
            <el-row>
               <el-col :span="12">
                  <div class="grid-content bg-purple">
                      <el-form-item label="任务完成情况" prop="completion">
                        <el-input placeholder="催交任务完成情况" v-model="addRuleForm.completion"></el-input>
                      </el-form-item>
                  </div>
                </el-col>
              <el-col :span="12">
                <div class="grid-content bg-purple">
                    <template>
                      <el-form-item label="下达日期" prop="issuedTime">
                      <el-date-picker
                          v-model="addRuleForm.issuedTime"
                          type="date"
                          placeholder="催交任务下达日期"
                          value-format="yyyy-MM-dd"
                          @change="changedate"
                      >
                      </el-date-picker>
                         </el-form-item>
                    </template>
                  </div>
                </el-col>
              </el-row>
             <el-row>
                <el-col :span="12">
                <div class="grid-content bg-purple">
                    <template>
                      <el-form-item label="要求到货日期" prop="requirementsTime">
                      <el-date-picker
                          v-model="addRuleForm.requirementsTime"
                          type="date"
                          placeholder="催交任务单要求到货日期"
                          value-format="yyyy-MM-dd"
                          @change="changedate"
                      >
                      </el-date-picker>
                         </el-form-item>
                    </template>
                  </div>
                </el-col>
              <el-col :span="12">
                      <div class="grid-content bg-purple">
                          <el-form-item label="付款进度" prop="paymentSchedule">
                            <el-input v-model="addRuleForm.paymentSchedule"></el-input>
                          </el-form-item>
                      </div>
                </el-col>
              </el-row>
            <el-form-item label="备注" prop="comment">
              <el-input v-model="addRuleForm.comment"></el-input>
            </el-form-item>
            </el-form>
        </span>
        <span slot="footer" class="dialog-footer">
          <el-button @click="addDialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="add" :disabled="btnDisabled" :loading="btnLoading">确 定</el-button>
        </span>
      </el-dialog>

      <!-- 设备编辑弹出框 -->
      <el-dialog
          title="更新设备"
          :visible.sync="editDialogVisible"
          width="50%"
          @close="closeEditDialog"
      >
        <span>
          <el-form
              size="mini"
              :model="editRuleForm"
              :rules="addRules"
              ref="editRuleFormRef"
              label-width="100px"
              class="demo-ruleForm"
          >
             <el-row>
                <el-col :span="12">
                  <div class="grid-content bg-purple">
                    <el-form-item label="设备名称" prop="name">
                      <el-input v-model="editRuleForm.name"></el-input>
                    </el-form-item>
                  </div>
                </el-col>
                <el-col :span="12">
                  <div class="grid-content bg-purple">
                    <el-form-item label="类别" prop="categoryKeys">
                      <el-cascader
                          :options="cateories"
                          clearable
                          filterable
                          :props="selectProps"
                          v-model="editRuleForm.categoryKeys"
                      ></el-cascader>
                    </el-form-item>
                  </div>
                </el-col>
             </el-row>
              <el-row>
                <el-col :span="12">
                  <div class="grid-content bg-purple">
                    <el-form-item label="总包" prop="unit">
                      <el-input v-model="editRuleForm.unit"></el-input>
                    </el-form-item>
                  </div>
                </el-col>
                <el-col :span="12">
                   <div class="grid-content bg-purple">
                      <el-form-item label="分包" prop="remark">
                        <el-input v-model="editRuleForm.remark"></el-input>
                      </el-form-item>
                    </div>
                 </el-col>
              </el-row>
              <el-row>
                <el-col :span="12">
                  <div class="grid-content bg-purple">
                      <el-form-item label="催交负责人" prop="head">
                        <el-input v-model="editRuleForm.head"></el-input>
                      </el-form-item>
                  </div>
                </el-col>
                <el-col :span="12">
                      <div class="grid-content bg-purple">
                          <el-form-item label="联系方式" prop="mobilePhone">
                            <el-input placeholder="负责人联系方式" v-model="editRuleForm.mobilePhone"></el-input>
                          </el-form-item>
                      </div>
                </el-col>
              </el-row>
             <el-row>
                <el-col :span="12">
                      <div class="grid-content bg-purple">
                          <el-form-item label="生产号令" prop="productionOrder">
                            <el-input v-model="editRuleForm.productionOrder"></el-input>
                          </el-form-item>
                      </div>
                </el-col>
               <el-col :span="12">
                      <div class="grid-content bg-purple">
                          <el-form-item label="发货地点" prop="deliveryAddress">
                            <el-input v-model="editRuleForm.deliveryAddress"></el-input>
                          </el-form-item>
                      </div>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="12">
                      <div class="grid-content bg-purple">
                          <el-form-item label="制造厂家" prop="manufacturer">
                            <el-input v-model="editRuleForm.manufacturer"></el-input>
                          </el-form-item>
                      </div>
                </el-col>
               <el-col :span="12">
                      <div class="grid-content bg-purple">
                          <el-form-item label="制造环节" prop="manufacturing">
                            <el-input v-model="editRuleForm.manufacturing"></el-input>
                          </el-form-item>
                      </div>
                </el-col>
              </el-row>
            <el-row>
               <el-col :span="12">
                      <div class="grid-content bg-purple">
                          <el-form-item label="设备进度" prop="plan">
                            <el-input v-model="editRuleForm.plan"></el-input>
                          </el-form-item>
                      </div>
                </el-col>
               <el-col :span="12">
                  <div class="grid-content bg-purple">
                    <template>
                      <el-form-item label="生产日期" prop="productionTime">
                      <el-date-picker
                          v-model="editRuleForm.productionTime"
                          type="date"
                          placeholder="进入生产工序时间"
                          value-format="yyyy-MM-dd"
                          @change="changedate"
                      >
                      </el-date-picker>
                         </el-form-item>
                    </template>
                  </div>
                </el-col>
              </el-row>

            <el-row>
               <el-col :span="12">
                  <div class="grid-content bg-purple">
                      <el-form-item label="运输周期" prop="period">
                        <el-input v-model="editRuleForm.period"></el-input>
                      </el-form-item>
                  </div>
               </el-col>
               <el-col :span="12">
                <div class="grid-content bg-purple">
                  <template>
                    <el-form-item label="完工日期" prop="completionTime">
                    <el-date-picker
                        v-model="editRuleForm.completionTime"
                        type="date"
                        placeholder="预计完工日期"
                        value-format="yyyy-MM-dd"
                        @change="changedate"
                    >
                    </el-date-picker>
                       </el-form-item>
                  </template>
                </div>
              </el-col>
              </el-row>
            <el-row>
              <el-col :span="12">
                  <div class="grid-content bg-purple">
                      <el-form-item label="是否进口物资" prop="importGoods">
                        <el-input placeholder="是否包含进口物资" v-model="editRuleForm.importGoods"></el-input>
                      </el-form-item>
                  </div>
               </el-col>
              <el-col :span="12">
                  <div class="grid-content bg-purple">
                    <template>
                      <el-form-item label="航运日期" prop="shippingTime">
                      <el-date-picker
                          v-model="editRuleForm.shippingTime"
                          type="date"
                          placeholder="进口物资航运时间"
                          value-format="yyyy-MM-dd"
                          @change="changedate"
                      >
                      </el-date-picker>
                         </el-form-item>
                    </template>
                  </div>
                </el-col>
            </el-row>
              <el-row>
                <el-col :span="12">
                  <div class="grid-content bg-purple">
                    <template>
                      <el-form-item label="发货日期" prop="arrivalTime">
                      <el-date-picker
                          v-model="editRuleForm.arrivalTime"
                          type="date"
                          placeholder="开始发货日期"
                          value-format="yyyy-MM-dd"
                          @change="changedate"
                      >
                      </el-date-picker>
                         </el-form-item>
                    </template>
                  </div>
                </el-col>
                <el-col :span="12">
                  <div class="grid-content bg-purple">
                    <template>
                      <el-form-item label="预计日期" prop="expectTime">
                      <el-date-picker
                          v-model="editRuleForm.expectTime"
                          type="date"
                          placeholder="预计到货日期"
                          value-format="yyyy-MM-dd"
                          @change="changedate"
                      >
                      </el-date-picker>
                         </el-form-item>
                    </template>
                  </div>
                </el-col>
              </el-row>
             <el-row>
               <el-col :span="12">
                  <div class="grid-content bg-purple">
                    <template>
                      <el-form-item label="实际日期" prop="actualTime">
                      <el-date-picker
                          v-model="editRuleForm.actualTime"
                          type="date"
                          placeholder="实际到货日期"
                          value-format="yyyy-MM-dd"
                          @change="changedate"
                      >
                      </el-date-picker>
                         </el-form-item>
                    </template>
                  </div>
                </el-col>
               <el-col :span="12">
                  <div class="grid-content bg-purple">
                      <el-form-item label="满足需求" prop="meetDemand">
                        <el-input  placeholder="是否满足到货需求" v-model="editRuleForm.meetDemand"></el-input>
                      </el-form-item>
                  </div>
                </el-col>
              </el-row>
            <el-row>
               <el-col :span="12">
                  <div class="grid-content bg-purple">
                      <el-form-item label="未到货原因" prop="goodsReason">
                        <el-input v-model="editRuleForm.goodsReason"></el-input>
                      </el-form-item>
                  </div>
                </el-col>
              <el-col :span="12">
                  <div class="grid-content bg-purple">
                      <el-form-item label="目前状态" prop="currentState">
                        <el-input v-model="editRuleForm.currentState"></el-input>
                      </el-form-item>
                  </div>
                </el-col>
            </el-row>
            <el-row>
               <el-col :span="12">
                      <div class="grid-content bg-purple">
                          <el-form-item label="图纸变更" prop="drawingChanges">
                            <el-input placeholder="图纸设计是否变更" v-model="editRuleForm.drawingChanges"></el-input>
                          </el-form-item>
                      </div>
                </el-col>
              <el-col :span="12">
                <div class="grid-content bg-purple">
                    <el-form-item label="变更原因" prop="changesReason">
                      <el-input v-model="editRuleForm.changesReason"></el-input>
                    </el-form-item>
                </div>
                </el-col>
            </el-row>
            <el-row>
               <el-col :span="12">
                  <div class="grid-content bg-purple">
                      <el-form-item label="变更进度" prop="drawingReview">
                        <el-input placeholder="图纸变更进度" v-model="editRuleForm.drawingReview"></el-input>
                      </el-form-item>
                  </div>
                </el-col>
              <el-col :span="12">
                  <div class="grid-content bg-purple">
                    <template>
                      <el-form-item label="收到日期" prop="expectedTime">
                      <el-date-picker
                          v-model="editRuleForm.actualTime"
                          type="date"
                          placeholder="预计图纸收到日期"
                          value-format="yyyy-MM-dd"
                          @change="changedate"
                      >
                      </el-date-picker>
                         </el-form-item>
                    </template>
                  </div>
                </el-col>
              </el-row>
            <el-row>
               <el-col :span="12">
                      <div class="grid-content bg-purple">
                    <template>
                      <el-form-item label="协议签署日期" prop="agreementTime">
                      <el-date-picker
                          v-model="editRuleForm.agreementTime"
                          type="date"
                          placeholder="技术协议签署日期"
                          value-format="yyyy-MM-dd"
                          @change="changedate"
                      >
                      </el-date-picker>
                         </el-form-item>
                    </template>
                  </div>
                </el-col>
              <el-col :span="12">
                      <div class="grid-content bg-purple">
                    <template>
                      <el-form-item label="合同签署日期" prop="signingTime">
                      <el-date-picker
                          v-model="editRuleForm.signingTime"
                          type="date"
                          placeholder="合同签署日期"
                          value-format="yyyy-MM-dd"
                          @change="changedate"
                      >
                      </el-date-picker>
                         </el-form-item>
                    </template>
                  </div>
                </el-col>
              </el-row>
            <el-row>
               <el-col :span="12">
                  <div class="grid-content bg-purple">
                      <el-form-item label="任务完成情况" prop="completion">
                        <el-input placeholder="催交任务完成情况" v-model="editRuleForm.completion"></el-input>
                      </el-form-item>
                  </div>
                </el-col>
              <el-col :span="12">
                <div class="grid-content bg-purple">
                    <template>
                      <el-form-item label="下达日期" prop="issuedTime">
                      <el-date-picker
                          v-model="editRuleForm.issuedTime"
                          type="date"
                          placeholder="催交任务下达日期"
                          value-format="yyyy-MM-dd"
                          @change="changedate"
                      >
                      </el-date-picker>
                         </el-form-item>
                    </template>
                  </div>
                </el-col>
              </el-row>
             <el-row>
                <el-col :span="12">
                <div class="grid-content bg-purple">
                    <template>
                      <el-form-item label="要求到货日期" prop="requirementsTime">
                      <el-date-picker
                          v-model="editRuleForm.requirementsTime"
                          type="date"
                          placeholder="催交任务单要求到货日期"
                          value-format="yyyy-MM-dd"
                          @change="changedate"
                      >
                      </el-date-picker>
                         </el-form-item>
                    </template>
                  </div>
                </el-col>
              <el-col :span="12">
                      <div class="grid-content bg-purple">
                          <el-form-item label="付款进度" prop="paymentSchedule">
                            <el-input v-model="editRuleForm.paymentSchedule"></el-input>
                          </el-form-item>
                      </div>
                </el-col>
              </el-row>
            <el-form-item label="备注" prop="comment">
              <el-input v-model="editRuleForm.comment"></el-input>
            </el-form-item>
            </el-form>
        </span>
        <span slot="footer" class="dialog-footer">
          <el-button @click="editDialogVisible = false">取 消</el-button>
          <el-button
              type="primary"
              @click="update"
              :disabled="btnDisabled"
              :loading="btnLoading"
          >确 定</el-button>
        </span>
      </el-dialog>


      <!-- 设备查看明细弹出框 -->
      <el-dialog
          title="查看设备明细"
          :visible.sync="findDialogVisible"
          width="50%"
          @close="closeEditDialog"
      >
        <span>
          <el-form
              size="mini"
              :model="findRuleForm"
              :rules="addRules"
              ref="findRuleFormRef"
              label-width="100px"
              class="demo-ruleForm"
              :disable=true
          >
            <el-row>
            <el-col :span="12">
                  <div class="grid-content bg-purple">
                    <el-form-item label="设备名称" prop="name">
                      <span class="display: inline-block; white-space: normal; width: 100px;
                    overflow: hidden; text-overflow: ellipsis;"
                            :title="findRuleForm.name">
                      <el-input readonly=true v-model="findRuleForm.name"> </el-input>
                    </span>
                    </el-form-item>
                  </div>
                </el-col>
                <el-col :span="12">
                  <div class="grid-content bg-purple">
                      <el-form-item label="类别" prop="categoryKeys">
                    <el-cascader
                        :options="cateories"
                        clearable
                        filterable
                        :props="selectProps"
                        v-model="findRuleForm.categoryKeys"
                        disabled="true"
                    >
                    </el-cascader>
                    </el-form-item>
                  </div>
                </el-col>
              </el-row>
            <el-row>
              <el-col :span="12">
                <div class="grid-content bg-purple">
                  <el-form-item label="总包" prop="unit" :show-overflow-tooltip="true">
                    <span class="display: inline-block; white-space: normal; width: 100px;
                    overflow: hidden; text-overflow: ellipsis;"
                          :title="findRuleForm.unit">
                      <el-input readonly=true v-model="findRuleForm.unit"> </el-input>
                    </span>
                  </el-form-item>
                </div>
              </el-col>
              <el-col :span="12">
                 <div class="grid-content bg-purple">
                    <el-form-item label="分包" prop="remark">
                      <span class="display: inline-block; white-space: normal; width: 100px;
                    overflow: hidden; text-overflow: ellipsis;"
                            :title="findRuleForm.remark">
                      <el-input readonly=true v-model="findRuleForm.remark"> </el-input>
                    </span>
                    </el-form-item>
                  </div>
               </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <div class="grid-content bg-purple">
                    <el-form-item label="催交负责人" prop="head">
                      <span class="display: inline-block; white-space: normal; width: 100px;
                    overflow: hidden; text-overflow: ellipsis;"
                            :title="findRuleForm.head">
                      <el-input readonly=true v-model="findRuleForm.head"> </el-input>
                    </span>
                    </el-form-item>
                </div>
              </el-col>
              <el-col :span="12">
                    <div class="grid-content bg-purple">
                        <el-form-item label="联系方式" prop="mobilePhone">
                          <span class="display: inline-block; white-space: normal; width: 100px;
                    overflow: hidden; text-overflow: ellipsis;"
                                :title="findRuleForm.mobilePhone">
                      <el-input readonly=true v-model="findRuleForm.mobilePhone"> </el-input>
                    </span>
                        </el-form-item>
                    </div>
              </el-col>
            </el-row>
             <el-row>
                <el-col :span="12">
                      <div class="grid-content bg-purple">
                          <el-form-item label="生产号令" prop="productionOrder">
                            <span class="display: inline-block; white-space: normal; width: 100px;
                    overflow: hidden; text-overflow: ellipsis;"
                                  :title="findRuleForm.productionOrder">
                            <el-input readonly=true v-model="findRuleForm.productionOrder"></el-input>
                            </span>
                          </el-form-item>
                      </div>
                </el-col>
               <el-col :span="12">
                      <div class="grid-content bg-purple">
                          <el-form-item label="发货地点" prop="deliveryAddress">
                            <span class="display: inline-block; white-space: normal; width: 100px;
                    overflow: hidden; text-overflow: ellipsis;"
                                  :title="findRuleForm.deliveryAddress">
                            <el-input readonly=true v-model="findRuleForm.deliveryAddress"></el-input>
                            </span>
                          </el-form-item>
                      </div>
                </el-col>
              </el-row>
            <el-row>
                <el-col :span="12">
                      <div class="grid-content bg-purple">
                          <el-form-item label="制造厂家" prop="manufacturer">
                            <span class="display: inline-block; white-space: normal; width: 100px;
                    overflow: hidden; text-overflow: ellipsis;"
                                  :title="findRuleForm.manufacturer">
                      <el-input readonly=true v-model="findRuleForm.manufacturer"> </el-input>
                    </span>
                          </el-form-item>
                      </div>
                </el-col>
               <el-col :span="12">
                      <div class="grid-content bg-purple">
                          <el-form-item label="制造环节" prop="currentState">
                            <span class="display: inline-block; white-space: normal; width: 100px;
                    overflow: hidden; text-overflow: ellipsis;"
                                  :title="findRuleForm.currentState">
                      <el-input readonly=true v-model="findRuleForm.currentState"> </el-input>
                    </span>
                          </el-form-item>
                      </div>
                </el-col>
              </el-row>
            <el-row>
               <el-col :span="12">
                      <div class="grid-content bg-purple">
                          <el-form-item label="设备进度" prop="plan">
                            <span class="display: inline-block; white-space: normal; width: 100px;
                    overflow: hidden; text-overflow: ellipsis;"
                                  :title="findRuleForm.plan">
                      <el-input readonly=true v-model="findRuleForm.plan"> </el-input>
                    </span>
                          </el-form-item>
                      </div>
                </el-col>
               <el-col :span="12">
                  <div class="grid-content bg-purple">
                    <template>
                      <el-form-item label="生产日期" prop="productionTime">
                      <el-date-picker
                          v-model="findRuleForm.productionTime"
                          type="date"
                          placeholder="进入生产工序时间"
                          value-format="yyyy-MM-dd"
                          @change="changedate"
                          disabled="true"
                      >
                      </el-date-picker>
                         </el-form-item>
                    </template>
                  </div>
                </el-col>
              </el-row>
               <el-row>
               <el-col :span="12">
                  <div class="grid-content bg-purple">
                      <el-form-item label="运输周期" prop="period">
                         <span class="display: inline-block; white-space: normal; width: 100px;
                    overflow: hidden; text-overflow: ellipsis;"
                               :title="findRuleForm.period">
                      <el-input readonly=true v-model="findRuleForm.period"> </el-input>
                    </span>
                      </el-form-item>
                  </div>
               </el-col>
               <el-col :span="12">
                <div class="grid-content bg-purple">
                  <template>
                    <el-form-item label="完工日期" prop="completionTime">
                    <el-date-picker
                        v-model="findRuleForm.completionTime"
                        type="date"
                        placeholder="预计完工日期"
                        value-format="yyyy-MM-dd"
                        @change="changedate"
                        disabled="true"
                    >
                    </el-date-picker>
                       </el-form-item>
                  </template>
                </div>
              </el-col>
              </el-row>
            <el-row>
              <el-col :span="12">
                  <div class="grid-content bg-purple">
                      <el-form-item label="是否进口物资" prop="importGoods">
                        <span class="display: inline-block; white-space: normal; width: 100px;
                    overflow: hidden; text-overflow: ellipsis;"
                              :title="findRuleForm.importGoods">
                      <el-input readonly=true v-model="findRuleForm.importGoods"> </el-input>
                    </span>
                      </el-form-item>
                  </div>
               </el-col>
              <el-col :span="12">
                  <div class="grid-content bg-purple">
                    <template>
                      <el-form-item label="航运日期" prop="shippingTime">
                      <el-date-picker
                          v-model="findRuleForm.shippingTime"
                          type="date"
                          placeholder="进口物资航运时间"
                          value-format="yyyy-MM-dd"
                          @change="changedate"
                          disabled="true"
                      >
                      </el-date-picker>
                         </el-form-item>
                    </template>
                  </div>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                  <div class="grid-content bg-purple">
                    <template>
                      <el-form-item label="发货日期" prop="arrivalTime">
                      <el-date-picker
                          v-model="findRuleForm.arrivalTime"
                          type="date"
                          placeholder="开始发货日期"
                          value-format="yyyy-MM-dd"
                          @change="changedate"
                          disabled="true"
                      >
                      </el-date-picker>
                         </el-form-item>
                    </template>
                  </div>
                </el-col>
                <el-col :span="12">
                  <div class="grid-content bg-purple">
                    <template>
                      <el-form-item label="预计日期" prop="expectTime">
                      <el-date-picker
                          v-model="findRuleForm.expectTime"
                          type="date"
                          placeholder="预计到货日期"
                          value-format="yyyy-MM-dd"
                          @change="changedate"
                          disabled="true"
                      >
                      </el-date-picker>
                         </el-form-item>
                    </template>
                  </div>
                </el-col>
              </el-row>
             <el-row>
               <el-col :span="12">
                  <div class="grid-content bg-purple">
                    <template>
                      <el-form-item label="实际日期" prop="actualTime">
                      <el-date-picker
                          v-model="findRuleForm.actualTime"
                          type="date"
                          placeholder="实际到货日期"
                          value-format="yyyy-MM-dd"
                          @change="changedate"
                          disabled="true"
                      >
                      </el-date-picker>
                         </el-form-item>
                    </template>
                  </div>
                </el-col>
               <el-col :span="12">
                  <div class="grid-content bg-purple">
                      <el-form-item label="满足需求" prop="meetDemand">
                        <span class="display: inline-block; white-space: normal; width: 100px;
                    overflow: hidden; text-overflow: ellipsis;"
                              :title="findRuleForm.meetDemand">
                      <el-input readonly=true v-model="findRuleForm.meetDemand"> </el-input>
                    </span>
                      </el-form-item>
                  </div>
                </el-col>
              </el-row>
            <el-row>
               <el-col :span="12">
                  <div class="grid-content bg-purple">
                      <el-form-item label="未到货原因" prop="goodsReason">
                        <span class="display: inline-block; white-space: normal; width: 100px;
                    overflow: hidden; text-overflow: ellipsis;"
                              :title="findRuleForm.goodsReason">
                      <el-input readonly=true v-model="findRuleForm.goodsReason"> </el-input>
                    </span>
                      </el-form-item>
                  </div>
                </el-col>
              <el-col :span="12">
                  <div class="grid-content bg-purple">
                      <el-form-item label="目前状态" prop="currentState">
                        <span class="display: inline-block; white-space: normal; width: 100px;
                    overflow: hidden; text-overflow: ellipsis;"
                              :title="findRuleForm.currentState">
                      <el-input readonly=true v-model="findRuleForm.currentState"> </el-input>
                    </span>
                      </el-form-item>
                  </div>
                </el-col>
            </el-row>
            <el-row>
               <el-col :span="12">
                      <div class="grid-content bg-purple">
                          <el-form-item label="图纸变更" prop="drawingChanges">
                            <span class="display: inline-block; white-space: normal; width: 100px;
                    overflow: hidden; text-overflow: ellipsis;"
                                  :title="findRuleForm.drawingChanges">
                      <el-input readonly=true v-model="findRuleForm.drawingChanges"> </el-input>
                    </span>
                          </el-form-item>
                      </div>
                </el-col>
              <el-col :span="12">
                <div class="grid-content bg-purple">
                    <el-form-item label="变更原因" prop="changesReason">
                      <span class="display: inline-block; white-space: normal; width: 100px;
                    overflow: hidden; text-overflow: ellipsis;"
                            :title="findRuleForm.changesReason">
                      <el-input readonly=true v-model="findRuleForm.changesReason"> </el-input>
                    </span>
                    </el-form-item>
                </div>
                </el-col>
            </el-row>
            <el-row>
               <el-col :span="12">
                  <div class="grid-content bg-purple">
                      <el-form-item label="变更进度" prop="drawingReview">
                         <span class="display: inline-block; white-space: normal; width: 100px;
                    overflow: hidden; text-overflow: ellipsis;"
                               :title="findRuleForm.drawingReview">
                      <el-input readonly=true v-model="findRuleForm.drawingReview"> </el-input>
                    </span>
                      </el-form-item>
                  </div>
                </el-col>
              <el-col :span="12">
                  <div class="grid-content bg-purple">
                    <template>
                      <el-form-item label="收到日期" prop="expectedTime">
                      <el-date-picker
                          v-model="findRuleForm.actualTime"
                          type="date"
                          placeholder="预计图纸收到日期"
                          value-format="yyyy-MM-dd"
                          @change="changedate"
                          disabled="true"
                      >
                      </el-date-picker>
                         </el-form-item>
                    </template>
                  </div>
                </el-col>
              </el-row>
            <el-row>
               <el-col :span="12">
                      <div class="grid-content bg-purple">
                    <template>
                      <el-form-item label="协议签署日期" prop="agreementTime">
                      <el-date-picker
                          v-model="findRuleForm.agreementTime"
                          type="date"
                          placeholder="技术协议签署日期"
                          value-format="yyyy-MM-dd"
                          @change="changedate"
                          disabled="true"
                      >
                      </el-date-picker>
                         </el-form-item>
                    </template>
                  </div>
                </el-col>
              <el-col :span="12">
                      <div class="grid-content bg-purple">
                    <template>
                      <el-form-item label="合同签署日期" prop="signingTime">
                      <el-date-picker
                          v-model="findRuleForm.signingTime"
                          type="date"
                          placeholder="合同签署日期"
                          value-format="yyyy-MM-dd"
                          @change="changedate"
                          disabled="true"
                      >
                      </el-date-picker>
                         </el-form-item>
                    </template>
                  </div>
                </el-col>
              </el-row>
            <el-row>
               <el-col :span="12">
                  <div class="grid-content bg-purple">
                      <el-form-item label="任务完成情况" prop="completion">
                        <span class="display: inline-block; white-space: normal; width: 100px;
                    overflow: hidden; text-overflow: ellipsis;"
                              :title="findRuleForm.completion">
                      <el-input readonly=true v-model="findRuleForm.completion"> </el-input>
                    </span>
                      </el-form-item>
                  </div>
                </el-col>
              <el-col :span="12">
                <div class="grid-content bg-purple">
                    <template>
                      <el-form-item label="下达日期" prop="issuedTime">
                      <el-date-picker
                          v-model="findRuleForm.issuedTime"
                          type="date"
                          placeholder="催交任务下达日期"
                          value-format="yyyy-MM-dd"
                          @change="changedate"
                          disabled="true"
                      >
                      </el-date-picker>
                         </el-form-item>
                    </template>
                  </div>
                </el-col>
              </el-row>
             <el-row>
                <el-col :span="12">
                <div class="grid-content bg-purple">
                    <template>
                      <el-form-item label="要求到货日期" prop="requirementsTime">
                      <el-date-picker
                          v-model="findRuleForm.requirementsTime"
                          type="date"
                          placeholder="催交任务单要求到货日期"
                          value-format="yyyy-MM-dd"
                          @change="changedate"
                          disabled="true"
                      >
                      </el-date-picker>
                         </el-form-item>
                    </template>
                  </div>
                </el-col>
              <el-col :span="12">
                      <div class="grid-content bg-purple">
                          <el-form-item label="付款进度" prop="paymentSchedule">
                            <el-input v-model="editRuleForm.paymentSchedule"></el-input>
                          </el-form-item>
                      </div>
                </el-col>
              </el-row>
            <el-form-item label="备注" prop="comment">
              <span class="display: inline-block; white-space: normal; width: 100px;
                    overflow: hidden; text-overflow: ellipsis;"
                    :title="findRuleForm.comment">
              <el-input readonly=true v-model="findRuleForm.comment"></el-input>
              </span>
            </el-form-item>
          </el-form>
        </span>
        <span slot="footer" class="dialog-footer">
          <el-button @click="findDialogVisible = false">关闭</el-button>
        </span>
      </el-dialog>
    </el-card>
  </div>
</template>

<script>
export default {

  data() {
    return {
      uploadApi: this.BASE_API_URL + 'system/upload/image',
      btnLoading: false,
      btnDisabled: false,
      loading: true,
      headerObject: {
        Authorization: LocalStorage.get(LOCAL_KEY_XINGUAN_ACCESS_TOKEN)
      }, //图片上传请求头
      cateories: [], //类别选择
      selectProps: {
        expandTrigger: "hover",
        value: "id",
        label: "name",
        children: "children",
        checkStrictly: false
      }, //级联选择器配置项
      searchSelectProps: {
        expandTrigger: "hover",
        value: "id",
        label: "name",
        children: "children",
        checkStrictly: true
      }, //级联搜索选择器配置项

      findDialogVisible: false,
      editDialogVisible: false,
      addDialogVisible: false, //添加弹框是否显示
      total: 0, //总共多少条数据
      productData: [], //表格数据
      queryMap: {
        pageNum: 1,
        pageSize: 10,
        name: "",
        categoryId: "",
        supplier: "",
        status: 0,

      }, //查询对象
      addRuleForm: {}, //添加表单数据
      editRuleForm: {}, //修改表单数据
      findRuleForm: {},
      uploadDisabled: false,
      limitCount: 1,
      dialogVisible: false,
      addRules: {
        name: [
          {required: true, message: "请输入设备名称", trigger: "blur"},
          {min: 2, max: 20, message: "长度在 2 到 10 个字符", trigger: "blur"}
        ],
        unit: [
          {required: true, message: "请输入设备总包", trigger: "blur"},
          {min: 1, max: 50, message: "长度在 1 到 50 个字符", trigger: "blur"}
        ],
        model: [
          {required: true, message: "请输入设备类别", trigger: "blur"},
        ],
        head: [
          {required: true, message: "请输入设备负责人", trigger: "blur"},
          {min: 2, max: 20, message: "长度在 2 到 20 个字符", trigger: "blur"}
        ],
        mobilePhone: [
          {required: true, message: "请输入设备类别", trigger: "blur"},
          {min: 7, max: 11, message: "11位电话号码", trigger: "blur"}
        ],
        remark: [
          {required: true, message: "请输入设备分包", trigger: "blur"},
          {min: 2, max: 100, message: "设备分包长度在 2 到 100 个字符", trigger: "blur"}
        ],
        plan: [
          // {required: true, message: "请输入设备进度", trigger: "blur"},
          {min: 2, max: 10, message: "设备进度长度在 2 到 50 个字符", trigger: "blur"}
        ],
        manufacturer: [
          // {required: true, message: "请输制造厂家", trigger: "blur"},
          {min: 2, max: 100, message: "制造厂家长度在 2 到 100 个字符", trigger: "blur"}
        ],
        currentState: [
          // {required: true, message: "请输入目前状态", trigger: "blur"},
          {min: 2, max: 25, message: "目前状态长度在 2 到 25 个字符", trigger: "blur"}
        ],
        drawingReview: [
          // {required: true, message: "请输入图纸审核", trigger: "blur"},
          {min: 2, max: 50, message: "图纸审核长度在 2 到 50 个字符", trigger: "blur"}
        ],
        paymentSchedule: [
          // {required: true, message: "请输入付款进度", trigger: "blur"},
          {min: 2, max: 100, message: "付款进度长度在 2 到 50 个字符", trigger: "blur"}
        ],
        period: [
          // {required: true, message: "请输入付款进度", trigger: "blur"},
          {min: 1, max: 100, message: "运输周期长度在 1 到 80 个字符", trigger: "blur"}
        ],
        importGoods: [
          // {required: true, message: "请输入付款进度", trigger: "blur"},
          {min: 1, max: 50, message: "是否包含进口物资长度在 1 到 50 个字符", trigger: "blur"}
        ],
        meetDemand: [
          // {required: true, message: "请输入付款进度", trigger: "blur"},
          {min: 1, max: 50, message: "是否满足到货需求长度在 1 到 50 个字符", trigger: "blur"}
        ],
        drawingChanges: [
          // {required: true, message: "请输入付款进度", trigger: "blur"},
          {min: 1, max: 50, message: "图纸变更长度在 1 到 100 个字符", trigger: "blur"}
        ],
        goodsReason: [
          // {required: true, message: "请输入付款进度", trigger: "blur"},
          {min: 1, max: 100, message: "是否满足到货需求长度在 1 到 100 个字符", trigger: "blur"}
        ],
        completion: [
          // {required: true, message: "请输入付款进度", trigger: "blur"},
          {min: 1, max: 100, message: "是否满足到货需求长度在 1 到 20 个字符", trigger: "blur"}
        ],
        changesReason: [
          // {required: true, message: "请输入付款进度", trigger: "blur"},
          {min: 1, max: 100, message: "是否满足到货需求长度在 1 到 100 个字符", trigger: "blur"}
        ],
        actualTime: [
          {required: false, message: "请输入实际到货时间", trigger: "blur"}
        ],
        comment: [
          {min: 0, max: 200, message: "备注 200 个字符内", trigger: "blur"}
        ],
        signingTime: [
          {required: false, message: "请输入合同签署时间", trigger: "blur"}
        ],
        arrivalTime: [
          {required: true, message: "请输入到货时间", trigger: "blur"}
        ],
        categoryKeys: [
          {required: true, message: "请选择设备分类", trigger: "blur"}
        ]
      }, //添加验证
      imgFilesList: [],
      categorykeys: [] //搜索类别
    };
  },
  methods: {
    //重置查询表单
    resetForm() {
      this.queryMap = {
        pageNum: 1,
        pageSize: 5,
        name: "",
        categoryId: "",
        supplier: "",
        status: 0,
      };
    },
    /**
     * 打开添加设备弹框
     */
    openAdd() {
      this.getCatetorys();
      this.addDialogVisible = true;
    },
    /**
     * 搜索设备
     */
    search() {
      this.queryMap.pageNum = 1;
      this.getproductList();
    },

    findByStatus(status) {
      this.queryMap.pageNum = 1;
      this.queryMap.status = status;
      this.getproductList();
    },
    findBySort(custom) {
      if (custom.prop == 'arrivalTime') {
        if (custom.order == 'ascending') {
          this.queryMap.filter = "expect_time"
          this.queryMap.filterType = 'asc';
        } else {
          this.queryMap.filter = "expect_time "
          this.queryMap.filterType = ' desc';
        }
      }
      if (custom.prop == 'createTime') {
        if (custom.order == 'ascending') {
          this.queryMap.filter = "create_time"
          this.queryMap.filterType = 'asc';
        } else {
          this.queryMap.filter = "create_time "
          this.queryMap.filterType = ' desc';
        }
      }
      this.queryMap.pageNum = 1;
      this.getproductList();
    },

    /**
     * 设备添加审核
     */
    async publish(id) {
      const {data: res} = await this.$http.put("business/product/publish/" + id);
      if (!res.success) {
        return this.$message.error("审核失败:" + res.data.errorMsg);
      } else {
        await this.getproductList();
        return this.$message.success("设备已审核通过");
      }
    },
    /**
     * 删除设备
     */
    async del(id) {
      var res = await this.$confirm(
          "此操作将永久删除该设备, 是否继续?",
          "提示",
          {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning"
          }
      ).catch(() => {
        this.$message({
          type: "info",
          message: "已取消删除"
        });
      });
      if (res === "confirm") {
        const {data: res} = await this.$http.delete("business/product/delete/" + id);
        if (res.success) {
          this.$message.success("设备删除成功");
          await this.getproductList();
        } else {
          this.$message.error(res.data.errorMsg);
        }
      }
    },
    /**
     * 更新设备
     */
    async update() {
      this.$refs.editRuleFormRef.validate(async valid => {
        if (!valid) {
          return;
        } else {
          this.btnDisabled = true;
          this.btnLoading = true;
          const {data: res} = await this.$http.put(
              "business/product/update/" + this.editRuleForm.id,
              this.editRuleForm
          );
          if (res.success) {
            this.$notify({
              title: "成功",
              message: "设备信息更新",
              type: "success"
            });
            this.editRuleForm = {};
            await this.getproductList();
          } else {
            this.$message.error("设备信息更新失败:" + res.data.errorMsg);
          }
          this.editDialogVisible = false;
          this.btnDisabled = false;
          this.btnLoading = false;
        }
      });
    },
    /**
     * 编辑设备
     */
    async edit(id) {
      const {data: res} = await this.$http.get("business/product/edit/" + id);
      if (res.success) {
        this.editRuleForm = res.data;
        const item = res.data;
        this.imgFilesList.push({
          name: item.name,
          id: item.id
        });
      } else {
        return this.$message.error("设备信息编辑失败" + res.data.errorMsg);
      }
      const array = [];
      array.push(res.data.oneCategoryId);
      array.push(res.data.twoCategoryId);
      array.push(res.data.threeCategoryId);
      this.editRuleForm.categoryKeys = array;
      this.editDialogVisible = true;
    },
    /**
     * 添加设备
     */
    async add() {
      this.$refs.addRuleFormRef.validate(async valid => {

        if (!valid) {
          return;
        } else {
          this.btnDisabled = true;
          this.btnLoading = true;
          const {data: res} = await this.$http.post(
              "business/product/add",
              this.addRuleForm
          );
          if (res.success) {
            this.$message.success("设备添加成功");
            this.addRuleForm = {};
            await this.getproductList();
          } else {
            return this.$message.error("设备添加失败:" + res.data.errorMsg);
          }
          this.addDialogVisible = false;
          this.btnDisabled = false;
          this.btnLoading = false;
        }
      });
    },

    /**
     * 添加设备
     */
    async find(id) {
      const {data: res} = await this.$http.get("business/product/findById/" + id);
      if (res.success) {
        this.findRuleForm = res.data;
        const item = res.data;
        this.imgFilesList.push({
          name: item.name,
          id: item.id
        });
      }
      const array = [];
      array.push(res.data.oneCategoryId);
      array.push(res.data.twoCategoryId);
      array.push(res.data.threeCategoryId);
      this.findRuleForm.categoryKeys = array;
      this.findDialogVisible = true;
    },

    /**
     * 移除回收站
     */
    async remove(id) {
      const {data: res} = await this.$http.put("business/product/remove/" + id);
      if (!res.success) {
        return this.$message.error("移入回收站失败:" + res.data.errorMsg);
      } else {
        await this.getproductList();
        return this.$message.success("移入回收站成功");
      }
    },
    /**
     * 从回收站恢复
     */
    async back(id) {
      const {data: res} = await this.$http.put("product/back/" + id);
      if (!res.success) {
        return this.$message.error("从回收站恢复失败:" + res.data.errorMsg);
      } else {
        await this.getproductList();
        return this.$message.success("从回收站中已恢复");
      }
    },
    /**
     * 加载设备列表
     */
    async getproductList() {
      const {data: res} = await this.$http.get("business/product/findProductList", {
        params: this.queryMap
      });
      if (!res.success) {
        return this.$message.error("获取设备列表失败");
      } else {
        this.total = res.data.total;
        this.productData = res.data.rows;
      }
    },
    /**
     * 加载设备类别
     */
    async getCatetorys() {
      const {data: res} = await this.$http.get(
          "business/productCategory/categoryTree"
      );
      if (!res.success) {
        return this.$message.error("获取设备类别失败");
      } else {
        this.cateories = res.data.rows;
      }
    },
    handleChange(file, fileList) {
      this.uploadDisabled = fileList.length >= this.limitCount;
    },
    handleRemove(file, fileList) {
      this.uploadDisabled = fileList.length >= this.limitCount;
    },
    //改变页码
    handleSizeChange(newSize) {
      this.queryMap.pageSize = newSize;
      this.getproductList();
    },
    //翻页
    handleCurrentChange(current) {
      this.queryMap.pageNum = current;
      this.getproductList();
    },
    //关闭弹出框
    closeAddDialog() {
      this.$refs.addRuleFormRef.clearValidate();
      this.addRuleForm = {};
      this.$refs.upload.clearFiles();
    },
    //关闭弹出框
    closeEditDialog() {
      this.$refs.editRuleFormRef.clearValidate();
      this.editRuleForm = {};
      this.$refs.upload2.clearFiles();
      this.imgFilesList = [];
    },

    closeFindDialog() {
      this.$refs.findRuleFormRef.clearValidate();
      this.editRuleForm = {};
      this.$refs.upload2.clearFiles();
      this.imgFilesList = [];
    },

    /**
     * 分类搜索改变时
     */
    selectChange() {
      var str = "";
      for (var i = 0; i < this.categorykeys.length; i++) {
        str += this.categorykeys[i] + ",";
      }
      str = str.substring(0, str.length - 1);
      this.queryMap.categorys = str;
    }
  },
  created() {
    this.getproductList();
    this.getCatetorys();
    setTimeout(() => {
      this.loading = false;
    }, 500);
  }
};
</script>


