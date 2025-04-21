import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  {
    path: 'tasks',
    loadComponent: () =>
      import('./task-feature/task-list/task-list.component').then(
        (component) => component.TaskListComponent
      ),
  },
];
