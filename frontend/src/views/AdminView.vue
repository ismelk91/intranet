<template>
    <NewsCreate />
    <InvitationForm />
    <ProjectComponent />
    <NewsList />
 <AdminUserList />
 <UserTimeRegistrations />
</template>

<script>
import InvitationForm from '@/components/InvitationForm.vue';
import AdminUserList from '@/components/AdminUserList.vue';
import NewsCreate from '@/components/NewsCreate.vue';
import NewsList from '@/components/NewsList.vue';
import ProjectComponent from '../components/ProjectComponent.vue';
import UserTimeRegistrations from '../components/UserTimeRegistrations.vue';

export default {
    components: {
        InvitationForm,
        AdminUserList,
        NewsCreate,
        NewsList,
        ProjectComponent,
        UserTimeRegistrations,
    },
};
</script>

<style scoped>
</style>
