<template>
    <div class="overlay" @click="closeOverlay"></div>
  </template>
  
  <script>
  export default {
    methods: {
      closeOverlay() {
        this.$emit('close-overlay');
      }
    }
  }
  </script>
  
  <style scoped>
  .overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.3);
    cursor: pointer; 
    z-index: 1; 
  }
  </style>  