import { TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';

import { TaskManagementService } from './task-management.service';
import { Page } from '../models/Page';
import { Task } from '../models/Task';
import { environment } from '../../../environment/environment';

describe('TaskManagementService', () => {
  let service: TaskManagementService;
  let httpMock: HttpTestingController;
  let baseUrl: any;
  let task: Task = { id: 1, description: 'desc', completed: false };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TaskManagementService],
    });

    service = TestBed.inject(TaskManagementService);
    httpMock = TestBed.inject(HttpTestingController);
    baseUrl = `${environment.apiUrl}/tasks`;
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch paginated tasks', () => {
    const mockResponse: Page<Task> = {
      content: [task],
      totalElements: 1,
      totalPages: 1,
      number: 0,
      size: 10,
    };

    service.getPageableTasks(0, 10, false).subscribe((data) => {
      expect(data.content.length).toBe(1);
    });

    const req = httpMock.expectOne(`${baseUrl}?page=0&size=10&completed=false`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });

  it('should fetch task by ID', () => {
    service.getTaskById(1).subscribe((data) => {
      expect(data.id).toBe(1);
    });

    const req = httpMock.expectOne(`${baseUrl}/id/1`);
    expect(req.request.method).toBe('GET');
    req.flush(task);
  });

  it('should create a new task', () => {
    service.createTask(task).subscribe((response) => {
      expect(response).toBeNull(); // car le service retourne void
    });

    const req = httpMock.expectOne(baseUrl);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(task);
    req.flush(null); // void response
  });

  it('should update a task', () => {
    const taskToUpdate: Task = { description: 'update', completed: true };
    service.updateTask(1, task).subscribe((data) => {
      expect(data.description).toBe('update');
      expect(data.completed).toBeTrue();
    });

    const req = httpMock.expectOne(`${baseUrl}/1`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(task);
    req.flush({ id: 1, description: 'update', completed: true });
  });
});
