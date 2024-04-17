<template>
    <div v-if="user" class="user-profile-container">

        <div class="top-left">
            <router-link :to="goBackRoute">
                <button class="back-btn btn btn-primary"><i class="bi bi-chevron-left"></i> Back</button>
            </router-link>
        </div>
        
        <div class="content-container">

            <label for="fileInput" class="img-container">
                <img v-if="user.profilePictureUrl" :src="user.profilePictureUrl" :alt="user.firstName + user.lastName + ' Profile Picture'" />
                <img v-else src="../assets/no-profile-picture.png" alt="No Profile Picture" />

                <div class="user-name">
                    {{ user.firstName }} {{ user.lastName }}
                </div>
            </label>

            <div class="form-container">
                <div class="row mb-3">
                    <div class="label">
                        <h6 class="mb-0">First name</h6>
                    </div>
                    <div class="value text-secondary">
                        <input type="text" class="form-control" :value="user.firstName" disabled>
                    </div>
                </div>

                <div class="row mb-3">
                    <div class="label">
                        <h6 class="mb-0">Last name</h6>
                    </div>
                    <div class="value text-secondary">
                        <input type="text" class="form-control" :value="user.lastName" disabled>
                    </div>
                </div>

                <div class="row mb-3">
                    <div class="label">
                        <h6 class="mb-0">Birthdate</h6>
                    </div>
                    <div class="value text-secondary">
                        <input type="date" class="form-control" :value="user.birthDate" disabled>
                    </div>
                </div>

                <div class="row mb-3">
                    <div class="label">
                        <h6 class="mb-0">Email</h6>
                    </div>
                    <div class="value text-secondary">
                        <input type="text" class="form-control" :value="user.email" disabled>
                    </div>
                </div>

                <div class="row mb-3">
                    <div class="label">
                        <h6 class="mb-0">Phone number</h6>
                    </div>
                    <div class="value text-secondary">
                        <input type="text" class="form-control" :value="user.phoneNumber" disabled>
                    </div>
                </div>

            </div>
        </div>
    </div>
</template>
<script>
import {  ref, onMounted, computed } from 'vue';
import { fetchUserProfile } from '@/stores/user';
import { useRoute } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

export default {
    setup() {
        const userDetails = ref(null);
        const route = useRoute();
        const authStore = useAuthStore();

        const fetchUserProfileData = async (email) => {
            try {
                const userData = await fetchUserProfile(email);
                userDetails.value = userData;
            } catch (error) {
                console.error('Error fetching user details:', error);
            }
        };

        const goBackRoute = computed(() => {
            return authStore.role === 'ROLE_ADMIN' ? '/admin' : '/colleagues';
        });

        onMounted(() => {
            const email = route.params.email;
            fetchUserProfileData(email);
        });

        return {
            user: userDetails,
            goBackRoute,
        };
    },
};
</script>

<style scoped>
.user-profile-container {
    position: relative;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    margin: 100px auto;
    background-color: var(--blue);
    color: var(--white);
    width: 980px;
    height: 500px;
    border-radius: 9px;
    padding: 20px;
}

.top-left {
    position: absolute;
    top: 15px;
    left: 15px;
}

.back-btn {
    background-color: #223266;
    color: var(--white);
    padding: 6px 10px;
    border: none;
    font-size: 12px;
    font-weight: 600;
    letter-spacing: 1.5px;
    text-transform: uppercase;
}

.back-btn:hover {
    background-color: var(--orange);
}

.content-container {
    display: flex;
    width: 100%;
}

.img-container {
    width: 30%;
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-right: 40px;
}

.img-container img {
    width: 150px;
    height: 150px;
    border-radius: 50%;
    margin-bottom: 30px;
}

.user-name {
    font-size: 22px;
    font-weight: 400;
    letter-spacing: 1.5px;
}

.form-container {
    flex: 1;
    margin-left: 50px;
    width: 70%;
    text-transform: uppercase;
}

.form-control:disabled{
    width: calc(100% - 40px);
    padding: 5px 10px;
    background-color: var(--blue);
    border: 1px solid var(--white);
    color: var(--white);
}

.label {
    margin-bottom: 5px;
}
</style>
