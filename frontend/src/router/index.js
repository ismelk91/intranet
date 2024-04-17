import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from '../views/LoginView.vue'
import UserProfileView from '../views/UserProfileView.vue';
import { useAuthStore } from '@/stores/auth'

const routes = [
	{
		path: '/',
		name: 'home',
		component: HomeView
	},
	{
		path: '/login',
		name: 'login',
		component: LoginView
	},
	{
		path: '/profile',
		name: 'profile',
		component: () => import('../views/ProfileView.vue')
	},
	{
		path: '/user/:email',
		name: 'user-profile',
		component: UserProfileView,
	},
	{
		path: '/colleagues',
		name: 'colleagues',
		component: () => import('../views/ColleaguesView.vue')
	},
	{
		path: '/news',
		name: 'news',
		component: () => import('../views/NewsView.vue')
	},
	{
		path: '/register/:uuid',
		name: 'register',
		component: () => import('../views/RegisterView.vue')
	},
	{
		path: '/admin',
		name: 'admin',
		component: () => import('../views/AdminView.vue')
	}
]

const router = createRouter({
	history: createWebHistory(process.env.BASE_URL),
	routes
})
router.beforeEach(async (to, from, next) => {
	const publicPages = ['/login'];
	const authRequired = !publicPages.includes(to.path);

	const auth = useAuthStore();

	if (authRequired && !auth.user && to.path !== `/register/${to.params.uuid}`) {
		auth.returnUrl = to.fullPath;
		next('/login');
	} else if (to.path === '/admin' && auth.role !== 'ROLE_ADMIN') {
		console.error('User does not have access to the admin panel.');
		next('/');
	} else {
		next();
	}
});

export default router
