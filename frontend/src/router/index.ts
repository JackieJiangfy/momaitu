import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { getToken } from '@/api/request'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { title: '登录', public: true }
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    children: [
      {
        path: 'novels',
        name: 'NovelList',
        component: () => import('@/views/novel/NovelList.vue'),
        meta: { title: '我的小说' }
      },
      {
        path: 'novel/:novelId/characters',
        name: 'CharacterList',
        component: () => import('@/views/character/CharacterList.vue'),
        meta: { title: '角色管理' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/Profile.vue'),
        meta: { title: '个人中心' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { title: '404', public: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 })
})

// 全局前置守卫：未登录用户访问受保护页面 → 跳转登录
router.beforeEach((to, _from, next) => {
  // 设置页面标题
  const title = to.meta.title as string | undefined
  document.title = title ? `${title} - 墨脉图` : '墨脉图 - 小说角色关系图谱系统'

  // 判断目标路由是否公开
  const isPublic = to.meta.public === true
  if (isPublic) {
    // 已登录用户访问登录页 → 跳首页
    if (to.name === 'Login' && getToken()) {
      next({ path: '/' })
      return
    }
    next()
    return
  }

  // 受保护路由：必须有 token
  if (!getToken()) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
    return
  }

  next()
})

export default router
