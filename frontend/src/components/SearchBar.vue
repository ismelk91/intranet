<template>
    <input v-model="searchQuery" @input="handleInput">
</template>

<script>
export default {
    data() {
        return {
            searchQuery: '',
        };
    },
    methods: {
        handleInput() {
            this.$emit('search', this.searchQuery);
        },
    },
};
</script>

<style scoped>
</style>
