<template>
    <div class="welcome-container">
        <h1>Welcome, {{ userStore.firstName }}</h1>
    </div>
</template>

<script>
import { useUserStore } from '../stores/user';
import { onMounted } from 'vue';

export default {
    setup() {
        const userStore = useUserStore();

        onMounted(async() => {
         await userStore.getUserDetails();
    });

        return {
            userStore,
        }
    }
}
</script>

<style scoped>
.welcome-container {
    display: flex;
    justify-content: flex-start;
    align-items: center;
    margin: 100px auto 0;
    color: var(--white);
    width: 980px;
}

.welcome-container h1 {
    font-size: 32px;
    font-weight: bold;
}

</style>