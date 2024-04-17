<template>
    <img src="@/assets/logo.png" alt="Deltma logo" class="logo" />
    <div class="registration-container">
        <div>
		<h1>Deltma</h1>
		<h2>Intranät</h2>
		</div>
        <div class="registration-form">
            <form @submit.prevent="onSubmit">
                <h4>Sign up</h4>
                <div class="form-group mb-3">
                    <input type="email" class="form-control" id="email" v-model="user.email" placeholder="email" />
                </div>

                <div class="form-group mb-3">
                    <input type="firstName" class="form-control" id="firstName" v-model="user.firstName"  placeholder="first name"/>
                </div>

                <div class="form-group mb-3">
                    <input type="lastName" class="form-control" id="lastName" v-model="user.lastName" placeholder="last name" />
                </div>

                <div class="form-group mb-3">
                    <input type="date" class="form-control" id="birthdate" v-model="user.birthDate"  />
                </div>

                <div class="form-group mb-3">
                    <input type="password" class="form-control" id="password" v-model="user.password" placeholder="password"/>
                </div>

                <div class="form-group mb-3">
                    <input type="phoneNumber" class="form-control" id="phoneNumber" v-model="user.phoneNumber" placeholder="phone number"/>
                </div>

                <button type="submit" class="register-btn btn btn-primary">Sign up</button>
            </form>
        </div>
    </div>
</template>

<script>
import { reactive } from 'vue';
import { useAuthStore } from '@/stores/auth';

const user = reactive({
    email: '',
    password: '',
});

function onSubmit() {
    console.log('onSubmit triggered', user);
    if (user.email !== '' && user.firstName !== '' && user.lastName !== '' && user.password !== '' && user.phoneNumber !== '' && user.birthDate !== '') {
        useAuthStore().register(
            user.email,
            user.firstName,
            user.lastName,
            user.password,
            user.phoneNumber,
            user.birthDate
        );
    }
}

export default {
    name: 'RegistrationForm',
    setup() {
        return {
            user,
            onSubmit,
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

.registration-container {
    display: flex;
	flex-direction: row;
	justify-content: space-between;
	align-items: center;
	position: fixed;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
	width: 900px;
}

.registration-container h1 {
	color: var(--white);
}

.registration-container h2 {
	color: var(--orange);
}

.registration-container h1, .registration-container h2 {
	font-size: 64px;
	text-transform: uppercase;
	font-weight: 600;
    letter-spacing: 1px;
}

.registration-form {
	width: 350px;
	color: var(--white);
	position: relative;
}

.registration-form input {
	width: 100%;
	height: 40px;
	border-radius: 5px;
	margin: 10px 0;
}

.register-btn {
    width: 100%;
	margin-top: 20px;
	font-size: 20px;
	letter-spacing: 1.5px;
}
</style>
