<template>
    <div>
        <h1>News</h1>
        <NewsList :allow-edit="false" />
    </div>
</template>

<script>
import NewsList from '@/components/NewsList.vue'

export default {
	name: "LatestNewsView",
    components: {
        NewsList,
},

};
</script>


<style>
</style>
