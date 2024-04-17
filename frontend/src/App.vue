<script>
import { RouterView } from 'vue-router';
import SideBar from './components/SideBar.vue';
import { useAuthStore } from './stores/auth';
import { computed } from 'vue';
import ProfileDropdown from './components/ProfileDropdown.vue';

export default {
    name: 'App',
    components: {
        RouterView,
        SideBar,
        ProfileDropdown,
    },
    setup() {
        const authStore = useAuthStore();
        const isAuthenticated = computed(() => !!authStore.token);

        return {
            isAuthenticated,
        };
    }
};
</script>

<template>
    <profile-dropdown v-if="isAuthenticated" />
    <side-bar v-if="isAuthenticated" />
    <router-view />
</template>

