<template>
    <div class="forgot-password-form">
        <h4>{{ headingText}}</h4>
        <form @submit.prevent="onSubmit">
            <div class="form-group mb-3">
                <div class="reset-msg" v-if="passwordSent">
                    <p>An email containing your new password has been sent to the provided email address. Please check your inbox and spam/junk folder.</p>
                </div>

                <div class="reset-msg" v-if="!passwordSent">
                    <p>Please enter the email address associated with your account. We'll send you a new password shortly.</p>
                </div>
                
                <input type="email" v-if="!passwordSent" class="form-control" id="email" name="email" v-model="email"  placeholder="email"/>
            </div>
            <button type="submit" v-if="!passwordSent" class="send-btn btn btn-primary">Send</button>
        </form>
    </div>
</template>

<script>
import axios from 'axios';

export default {
    name: 'ForgotPassword',
    data() {
        return {
            email: '',
            passwordSent: false,
            headingText: 'Reset your password',
        };
    },
    methods: {
        onSubmit() {
            const formData = new FormData();
            formData.append('email', this.email);

            axios.post('http://localhost:8080/api/v1/reset-password', formData)
                .then(() => {
                    this.passwordSent = true;
                    this.headingText = 'Password successfully sent';
                })
                .catch(error => {
                    console.error('Error:', error);
                });
        },
    },
};
</script>

<style scoped>

.reset-msg {
    margin-top: 10px;
    margin-bottom: 20px;
    font-size: 14px;
}

.send-btn {
    margin-top: 5px;
    width: 80px;
}
</style>
