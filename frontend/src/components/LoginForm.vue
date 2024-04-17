<template>
<div class="">
<img src="@/assets/logo.png" alt="Deltma logo" class="logo" />
	<div class="login-container">
		<div>
		<h1>Deltma</h1>
		<h2>Intranät</h2>
		</div>
		<div class="login-form">
			<form @submit.prevent="onSubmit">
				<h4>Sign in</h4>
				<div class="form-group mb-3">
					<input type="email" class="form-control" id="email" v-model="user.email" placeholder="email" />
				</div>
				<div class="form-group mb-3">
					<input type="password" class="form-control" id="password" v-model="user.password" placeholder="password" />
					<a @click="showForgotPasswordPopup" class="forgotpassword">Forgot password?</a>
				</div>
				<div class="pt-1 mb-4">
					<button type="submit" class="signin-btn btn btn-primary">Sign in</button>
				</div>
			</form> 
		</div>
	
		<transition name="fade">
			<div v-if="showPopup" class="forgotpasswordPopup">
				<i class="forgetpassword-toggle bi bi-x" @click="closePopup"></i>
				<ForgotPassword />
			</div>
		</transition>
	</div>
</div>

<Overlay v-if="showPopup" @close-overlay="closePopup" />
</template>

<script>
import { reactive, ref } from 'vue';
import { useAuthStore } from '@/stores/auth';
import ForgotPassword from './ForgotPassword.vue';
import Overlay from './OverlayComponent.vue';

export default {
	name: 'LoginForm',
	components: {
		ForgotPassword,
		Overlay,
	},
	setup() {
		const showPopup = ref(false);

		const user = reactive({
			email: '',
			password: 'password',
		});

		function onSubmit() {
			console.log(user);
			if (user.email !== '' && user.password !== '') {
				useAuthStore().login(user.email, user.password);
			}
		}

		function showForgotPasswordPopup() {
			showPopup.value = true;
		}

		function closePopup() {
			showPopup.value = false;
		}

		return {
			user,
			onSubmit,
			showPopup,
			showForgotPasswordPopup,
			closePopup,
		};
	},
};
</script>

<style scoped>

.logo {
	width: 150px;
	height: 50px;
	margin-top: 60px;
	margin-left: 40px;
}

.login-container {
	display: flex;
	flex-direction: row;
	justify-content: space-between;
	align-items: center;
	margin: 25vh auto;
	width: 900px;
}

.login-container h1 {
	color: var(--white);
}

.login-container h2 {
	color: var(--orange);
}

.login-container h1, .login-container h2 {
	font-size: 64px;
	text-transform: uppercase;
	font-weight: 600;
	letter-spacing: 1px;
}

.login-form {
	width: 350px;
	color: var(--white);
	position: relative;
}

.login-form input {
	width: 100%;
	height: 40px;
	border-radius: 5px;
	margin: 10px 0;
}

.signin-btn {
	width: 100%;
	margin-top: 30px;
	font-size: 20px;
	letter-spacing: 1.5px;
}

.login-form a {
	position: absolute;
	right: 0;
	color: var(--white);
	text-decoration: none;
	font-size: 14px;
	font-weight: 500;
	cursor: pointer;
}

.login-form a:hover {
	text-decoration: underline;
}

.forgotpasswordPopup {
	position: absolute;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
	background: var(--blue);
	color: var(--white);
	padding: 20px;
	box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
	max-width: 500px;
	border-radius: 5px;
	z-index: 9;
}

.forgetpassword-toggle  {
    position: absolute;
    top: 0;
    right: 0;
    height: 35px;
    width: 40px;
	font-size: 25px;
    color: var(--white);
    cursor: pointer;
    transition: 0.3s;
	text-align: center;
    }

    .forgetpassword-toggle:hover {
    background-color: var(--red);
	border-top-right-radius: 5px;
    color: var(--white);
    cursor: pointer;
    }

</style>