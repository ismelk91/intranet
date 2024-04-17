import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './assets/main.css'
import './assets/logo.png'

import "bootstrap";

import VCalendar from 'v-calendar';
import 'v-calendar/style.css';

import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-icons/font/bootstrap-icons.css'

const pinia = createPinia()
const app = createApp(App)

app.use(VCalendar, {})
app.use(pinia)
app.use(router)
app.mount('#app')