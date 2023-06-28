/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import Dashboard from '@/views/Dashboard.vue'
import Authenticated from '@/layouts/Authenticated.vue'
import ErrorPage from '@/views/ErrorPage.vue'
import { nextTick } from 'vue'

const routes: Array<RouteRecordRaw> = [
    {
        path: '/',
        name: 'home',
        component: Authenticated,
        children: [
            {
                path: '/',
                name: 'dashboard',
                component: Dashboard,
            },
            {
                path: ':clusterId/topics',
                name: 'topics',
                component: () => import('../views/TopicsView.vue'),
            },
            {
                path: ':clusterId/topics/:name',
                component: () => import('../views/TopicDetailView.vue'),
                redirect: {
                    name: 'topicOverview',
                },
                children: [
                    {
                        path: 'schema',
                        redirect: {
                            name: 'topicSchemaValue',
                        },
                        component: () => import('../views/TopicSchemaView.vue'),
                        children: [
                            {
                                path: 'value',
                                name: 'topicSchemaValue',
                                component: () => import('../views/SchemaManagerView.vue'),
                            },
                            {
                                path: 'key',
                                name: 'topicSchemaKey',
                                component: () => import('../views/SchemaManagerView.vue'),
                            },
                        ],
                    },
                    {
                        path: 'overview',
                        name: 'topicOverview',
                        component: () => import('../views/TopicInformationView.vue'),
                    },
                    {
                        path: 'messages',
                        name: 'topicMessages',
                        component: () => import('../views/TopicMessageView.vue'),
                    },
                ],
            },
            {
                path: ':clusterId/consumers-group',
                name: 'consumersGroup',
                component: () => import('../views/ConsumersGroupView.vue'),
            },
            {
                path: ':clusterId/broker',
                name: 'broker',
                component: () => import('../views/BrokerView.vue'),
            },
        ],
    },
    {
        path: '/:pathMatch(.*)*',
        name: 'not-found',
        component: ErrorPage,
        meta: {
            title: 'Error',
        },
    },
    {
        path: '/error',
        name: 'error',
        component: ErrorPage,
        meta: {
            title: 'Error',
        },
    },
    {
        path: '/login',
        name: 'login',
        component: () => import('../views/Login.vue'),
        meta: {
            title: 'Please login',
        },
    },
    // {
    //     path: '/about',
    //     name: 'about',
    //     // route level code-splitting
    //     // this generates a separate chunk (about.[hash].js) for this route
    //     // which is lazy-loaded when the route is visited.
    //     component: () => import( '../views/AboutView.vue'),
    // },
]

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes,
})

router.afterEach(async (to) => {
    await nextTick(() => {
        // If meta title is absent then we will use name (with up case first letter)
        const title = (to.meta?.title ?? to.name?.toString() ?? '') as string
        const formattedTitle =
            title === ''
                ? ''
                : (title.charAt(0).toUpperCase() + title.slice(1))
                      .replace(/([a-z])([A-Z])/g, '$1 $2')
                      // space before last upper in a sequence followed by lower
                      .replace(/\b([A-Z]+)([A-Z])([a-z])/, '$1 $2$3')
                      // uppercase the first character
                      .replace(/^./, (str) => str.toUpperCase())
        const meta = import.meta.env.VITE_TITLE ?? 'Tansen'
        document.title = formattedTitle === '' ? `${meta}` : `${meta} - ${formattedTitle}`
    })
})

export default router
