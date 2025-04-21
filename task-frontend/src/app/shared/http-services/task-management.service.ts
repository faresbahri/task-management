import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Page } from '../models/Page';
import { Task } from '../models/Task';
import { environment } from '../../../environment/environment';

@Injectable({
  providedIn: 'root',
})
export class TaskManagementService {
  private readonly baseUrl = '/tasks';
  constructor(private readonly http: HttpClient) {}

  getPageableTasks(
    page: number = 0,
    size: number = 10,
    completed?: boolean
  ): Observable<Page<Task>> {
    let params = new HttpParams().set('page', page).set('size', size);

    if (completed !== undefined) {
      params = params.set('completed', completed);
    }
    return this.http.get<Page<Task>>(`${environment.apiUrl}${this.baseUrl}`, {
      params,
    });
  }

  getTaskById(id: number): Observable<Task> {
    return this.http.get<Task>(`${environment.apiUrl}${this.baseUrl}/id/${id}`);
  }

  createTask(task: Task): Observable<void> {
    return this.http.post<void>(`${environment.apiUrl}${this.baseUrl}`, task);
  }

  updateTask(id: number, task: Task): Observable<Task> {
    return this.http.put<Task>(
      `${environment.apiUrl}${this.baseUrl}/${id}`,
      task
    );
  }
}
