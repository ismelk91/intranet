<template>
	<WelcomeUser />
	<NewsList :allow-edit="false" :max-entries="1" :hide-search-and-toggle-show-all="true" />
	<Calender />
</template>

<script>
import NewsList from "@/components/NewsList.vue"
import Calender from '@/components/CalenderView.vue';
import WelcomeUser from "../components/WelcomeUser.vue"

export default {
	name: "HomeView",
	components: {
		NewsList,
		Calender,
		WelcomeUser
	},
};
</script>
