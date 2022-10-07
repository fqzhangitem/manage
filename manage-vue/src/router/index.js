import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
    {
        path: '/',
        redirect: '/login'
    },
    {
        path: '/login',
        name: 'Login',
        component: () => import( '../views/login.vue'), //登入页面
    },

    {
        path: '/home',
        component: () => import( '../views/home.vue'), //后台主体框架
        redirect: '/system/welcome',
        children: [
            {
                path: '/system/welcome',
                name: 'Welcome',
                component: () => import( '../views/welcome.vue'), //系统欢迎页
            }, {
                path: '/system/users',
                name: 'Users',
                component: () => import( '../views/system/user.vue'), //用户管理
            }, {
                path: '/system/roles',
                name: 'Roles',
                component: () => import( '../views/system/role.vue'), //角色管理
            }
            , {
                path: '/system/menus',
                name: 'Menus',
                component: () => import( '../views/system/menu.vue'), //菜单管理
            }
            , {
                path: '/system/departments',
                name: 'Departments',
                component: () => import( '../views/system/dept.vue'), // 部门管理
            },
            {
                path: '/system/files',
                name: 'Files',
                component: () => import('../views/system/file'),   //文件管理
            },
            {
                path: '/system/icon',
                name: 'Icons',
                component: () => import('../views/system/icon'),  //系统图标
            }
            /**********************************业务模块的路由******************************/
            , {
                path: '/business/product/list',
                name: 'ProductList',
                component: () => import( '../views/business/product/list.vue'), //设备催交
            }
            , {
                path: '/business/product/categories',
                name: 'ProductCategories',
                component: () => import( '../views/business/product/category'), //设备类别
            },
            /**********************************监控模块的路由******************************/
            {
                path: '/monitor/logs',
                name: 'Logs',
                component: () => import( '../views/monitor/log'),     //操作日志
            },
            {
                path: '/monitor/login-log',
                name: 'LoginLogs',
                component: () => import( '../views/monitor/login-log'), //登入日志
            },
            {
                path: '/monitor/swagger-ui',
                name: 'SwaggerUI',
                component: () => import( '../views/monitor/swagger-ui'), //接口文档
            },
            {
                path: '/monitor/druid',
                name: 'Druid',
                component: () => import( '../views/monitor/druid'), //mysql监控
            },
            /**********************************疫情模块的路由******************************/
            {
                path: "/error/401",
                component: () => import( '../views/error/401'),
            }
        ]
    },
]

const router = new VueRouter({
    mode: 'hash',
    base: process.env.BASE_URL,
    routes
})

import store from '../store'//引入store

//白名单页面
const whiteList=[
    '/business/product/add-stocks',
    '/business/product/publish'
];

//路由导航守卫
router.beforeEach((to, from, next) => {

    const token = LocalStorage.get(LOCAL_KEY_XINGUAN_ACCESS_TOKEN);
    if (to.path === '/login') {
        if (!token) {
            return next();
        } else {
            return next({path: '/home'})
        }
    }

    if (to.path === '/error/401') {
        return next();
    }

    if (!token) {
        return next('/login');
    } else {
        //判断是否有访问该路径的权限
        const urls = store.state.userInfo.url;
        //如果是管理员
        if (store.state.userInfo.isAdmin) {
            return next();
        } else {
            if (urls.indexOf(to.path) > -1|| whiteList.indexOf(to.path)>-1) {
                //则包含该元素
                window.sessionStorage.setItem("activePath", to.path);
                return next();
            } else {
                return next("/error/401");
            }
        }
    }
})

export default router
