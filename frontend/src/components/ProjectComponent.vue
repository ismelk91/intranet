<template>
  <div @click="togglePopup" class="viewproject-text">View Projects</div>

  <div v-if="isPopupVisible" class="project-popup">
    <i class="project-toggle bi bi-x" @click="togglePopup"></i>
    <div class="popup-content">
      <h1>Projects</h1>

      <ul class="project-list">
        <li v-for="project in projects" :key="project.id">
          <div class="project-info">
            <span>{{ project.project }}</span>
            <div class="button-container">
              <button @click="() => showUpdateForm(project)" class="edit-btn btn btn-edit">Edit</button>
              <button v-if="project.active" @click="() => inactivateProject(project.id)"
                class="inactivate-btn btn btn-edit">Inactivate</button>
              <button v-else @click="() => activeProjects(project.id)"
                class="activate-btn btn btn-primary">Activate</button>
              <button @click="() => deleteProject(project.id)" class="delete-btn btn btn-delete">Delete</button>
            </div>
          </div>
        </li>
      </ul>

      <div>
        <button @click="showCreateForm" class="create-btn btn btn-primary">New Project</button>
      </div>

      <form v-if="isCreateFormVisible" @submit.prevent="createNewProject">
        <label for="newProjectName">Project Name:</label>
        <input v-model="newProjectName" type="text" id="newProjectName" required />
        <button type="submit" class="createProject-btn btn btn-primary">Create</button>
        <button @click="hideCreateForm" type="button" class="cancel-btn btn btn-secondary">Cancel</button>
      </form>

      <form v-if="isUpdateFormVisible" @submit.prevent="updateExistingProject">
        <label for="updatedProjectName">New project name:</label>
        <input v-model="updatedProjectName" type="text" id="updatedProjectName" required />
        <button type="submit" class="update-btn btn btn-primary">Update</button>
        <button @click="hideUpdateForm" type="button" class="cancel-btn btn btn-secondary">Cancel</button>
      </form>

    </div>
  </div>
  <Overlay v-if="isPopupVisible" @close="togglePopup" />
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useProjectStore } from '../stores/project';
import Overlay from './OverlayComponent.vue';

const { getAllProjects, createProject, updateProject } = useProjectStore();

const projects = ref([]);
const newProjectName = ref('');
const isPopupVisible = ref(false);
const isCreateFormVisible = ref(false);
const isUpdateFormVisible = ref(false);
const updatedProjectName = ref('');
let updatingProjectId = ref(null);


const togglePopup = () => {
  isPopupVisible.value = !isPopupVisible.value;
};

const createNewProject = async () => {
  try {
    await createProject({ project: newProjectName.value });
    hideCreateForm();
    projects.value = await getAllProjects();
  } catch (error) {
    console.error('Error creating project:', error);
  }
};

const showCreateForm = () => {
  isCreateFormVisible.value = true;
};

const hideCreateForm = () => {
  isCreateFormVisible.value = false;
  newProjectName.value = '';
};

const showUpdateForm = (project) => {
  isUpdateFormVisible.value = true;
  updatedProjectName.value = project.project;
  updatingProjectId = project.id;
};

const hideUpdateForm = () => {
  isUpdateFormVisible.value = false;
  updatedProjectName.value = '';
  updatingProjectId = null;
};


const updateExistingProject = async () => {
  try {
    const projectDTO = {
      id: updatingProjectId,
      project: updatedProjectName.value,
    };

    await updateProject(projectDTO);
    hideUpdateForm();
    projects.value = await getAllProjects();
  } catch (error) {
    console.error('Error updating project:', error);
  }
};

const inactivateProject = async (id) => {
  try {
    await useProjectStore().inactivateProject(id);
    projects.value = await getAllProjects();
  } catch (error) {
    console.error('Error inactivating project:', error);
  }
};

const activeProjects = async (id) => {
  try {
    await useProjectStore().activateProject(id);
    projects.value = await getAllProjects();
  } catch (error) {
    console.error('Error activating project', error);
  }
}

const deleteProject = async (id) => {
  try {
    await useProjectStore().deleteProject(id);
    projects.value = await getAllProjects();
  } catch (error) {
    console.error('Error deleting project:', error);
  }
};

onMounted(async () => {
  try {
    projects.value = await getAllProjects();
  } catch (error) {
    console.error('Error fetching projects:', error);
  }
});

</script>

<style scoped>
.viewproject-text {
  position: absolute;
  top: 215px;
  padding-left: 40px;
  font-size: 22px;
  font-weight: 600;
  text-transform: uppercase;
  color: var(--white);
  cursor: pointer;
  z-index: 1;
}

.table-container {
  border: 2px solid #ffffff;
  border-radius: 9px;
}

.project-popup {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 600px;
  min-height: 300px;
  padding: 20px;
  background-color: var(--blue);
  color: var(--white);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  border-radius: 5px;
  z-index: 9;
}

.popup-content form label {
  margin-top: 15px;
}

.popup-content form {
  background-color: #1c254d;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.4);
  height: 60px;
  padding: 5px;
  border-radius: 5px;
}

.project-popup h1 {
  font-size: 26px;
  margin-bottom: 20px;
}

.project-popup input {
  width: 40%;
  margin: 0 10px 5px 10px;
  outline: none;
}

.create-btn {
  position: fixed;
  bottom: 15px;
  right: 15px;
}

.btn {
  background-color: #223266;
  color: var(--white);
  padding: 6px 10px;
  border-radius: 5px;
  border: none;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 1.5px;
  text-transform: uppercase;
  transition: background-color 0.3s ease, color 0.3s ease;
  margin-right: 10px;
}

.btn:hover {
  background-color: var(--orange);
}

.project-list {
  list-style: none;
  padding: 0;
}

.project-list li {
  margin: 10px 0;
}

.project-info {
  display: flex;
  flex-direction: row;
  align-items: center;
}

.button-container {
  position: fixed;
  right: 20px;
}

.project-toggle {
  position: absolute;
  display: flex;
  justify-content: center;
  align-items: center;
  top: 0;
  right: 0;
  height: 30px;
  width: 30px;
  color: #fff;
  font-size: 25px;
  cursor: pointer;
  transition: 0.3s;
  border-top-right-radius: 5px;
}

.project-toggle:hover {
  background-color: var(--red);
  color: var(--white);
  cursor: pointer;
}
</style>