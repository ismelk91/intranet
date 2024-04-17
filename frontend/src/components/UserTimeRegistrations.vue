<template>
  <div class="container">

    <table class="user-list">
      <thead>
        <tr>
          <th class="first">Email</th>
          <th>Total Time</th>
          <th class="total-project">Total Project</th>
          <th class="last">
            <select v-model="selectedMonth" @change="fetchUsersTotalTime" class="select-month">
              <option v-for="(month, index) in months" :value="index + 1" :key="index">{{ month }}</option>
            </select>
          </th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(user, index) in usersTotalTime" :key="index" class="user-list">
          <td>{{ user.email }}</td>
          <td>
            <ul>
              <li v-for="(totalTime, projectId) in user.totalPerProject" :key="projectId">
                <span v-if="projects[projectId]">{{ projects[projectId].project }}: </span>{{ totalTime }}
              </li>
            </ul>
          </td>
          <td class="total-project-h">{{ user.totalTime }}</td>
        </tr>
      </tbody>
    </table>

  </div>
</template>

<script>
import { defineComponent, onMounted, ref } from 'vue';
import { useTimeStore } from '../stores/time';
import { useProjectStore } from '../stores/project';

export default defineComponent({
  setup() {
    const timeStore = useTimeStore();
    const usersTotalTime = ref([]);
    const projectStore = useProjectStore();
    const projects = ref({});
    const selectedMonth = ref(new Date().getMonth() + 1);
    const months = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];

    const fetchUsersTotalTime = async () => {
      const year = new Date().getFullYear();
      await timeStore.getUsersTotalTimePerMonth(year, selectedMonth.value);
      usersTotalTime.value = timeStore.usersTotalTime;
    };

    const fetchProjects = async () => {
      const allProjects = await projectStore.getAllProjects(); allProjects.forEach(project => {
        projects.value[project.id] = project;
      });
      projects.value = { ...projects.value };
    };

    onMounted(async () => {
      await fetchUsersTotalTime();
      await fetchProjects();
    });

    return {
      usersTotalTime,
      selectedMonth,
      months,
      fetchUsersTotalTime,
      projects,
    };
  },
});
</script>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 20px auto;
}

.user-list {
  background-color: var(--blue);
  width: 980px;
  border-top-left-radius: 9px;
  border-top-right-radius: 9px;
  color: var(--white);
  position: relative;
}

.user-list ul {
  list-style: none;
  padding: 0;
}

.user-list th {
  padding: 10px;
  font-size: 14px;
  background-color: #222E58;
}

.user-list th.first {
  border-top-left-radius: 9px;
}

.user-list th.last {
  border-top-right-radius: 9px;
}

.user-list td {
  padding: 10px;
  font-size: 16px;
}

.total-project {
  text-align: center;
}

.total-project-h {
  text-align: center;
}

.select-month {
  padding: 3px 5px;
  border-radius: 50px;
  outline: none;
  width: 100px;
}
</style>