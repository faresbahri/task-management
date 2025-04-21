import {
  ComponentFixture,
  TestBed,
  fakeAsync,
  flush,
  tick,
} from '@angular/core/testing';
import { TaskListComponent } from './task-list.component';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { of } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { TaskManagementService } from '../../shared/http-services/task-management.service';

describe('TaskListComponent', () => {
  let component: TaskListComponent;
  let fixture: ComponentFixture<TaskListComponent>;
  let taskServiceSpy: jasmine.SpyObj<TaskManagementService>;
  let dialogSpy: jasmine.SpyObj<MatDialog>;
  let snackBarSpy: jasmine.SpyObj<MatSnackBar>;
  const task = { id: 1, description: 'Test', completed: false };

  beforeEach(async () => {
    const taskSpy = jasmine.createSpyObj('TaskManagementService', [
      'getPageableTasks',
      'createTask',
      'updateTask',
    ]);
    const dialogMock = jasmine.createSpyObj('MatDialog', ['open']);
    const matDialogRefSpy = jasmine.createSpyObj('MatDialogRef', [
      'afterClosed',
    ]);
    matDialogRefSpy.afterClosed.and.returnValue(of(task));
    dialogMock.open.and.returnValue(matDialogRefSpy);

    const snackBarMock = jasmine.createSpyObj('MatSnackBar', ['open']);
    await TestBed.configureTestingModule({
      imports: [
        TaskListComponent,
        FormsModule,
        MatSnackBarModule,
        MatDialogModule,
        MatPaginatorModule,
        MatTableModule,
        NoopAnimationsModule,
      ],
      providers: [
        { provide: TaskManagementService, useValue: taskSpy },
        { provide: MatDialog, useValue: dialogMock },
        { provide: MatSnackBar, useValue: snackBarMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TaskListComponent);
    component = fixture.componentInstance;
    taskServiceSpy = TestBed.inject(
      TaskManagementService
    ) as jasmine.SpyObj<TaskManagementService>;
    dialogSpy = TestBed.inject(MatDialog) as jasmine.SpyObj<MatDialog>;
    taskServiceSpy.getPageableTasks.and.returnValue(
      of({
        content: [{ id: 1, description: 'hello', completed: true }],
        totalElements: 1,
      } as any)
    );
    snackBarSpy = TestBed.inject(MatSnackBar) as jasmine.SpyObj<MatSnackBar>;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should load tasks on init', () => {
    const loadDataSourceSpy = spyOn(
      component,
      'loadDataSource'
    ).and.callThrough();
    component.ngOnInit();
    expect(taskServiceSpy.getPageableTasks).toHaveBeenCalledWith(0, 10);
    expect(component.dataSource.data.length).toBe(1);
    expect(loadDataSourceSpy).toHaveBeenCalled();
  });

  it('should update dataSource and paginator when data.content is not empty', () => {
    const mockPage = {
      content: [{ id: 1, description: 'Test', completed: false }],
      totalElements: 1,
    } as any;

    component.paginator = {
      length: 0,
      pageIndex: 0,
    } as any;

    component.currentPage = 2;

    component.loadDataSource(mockPage);

    expect(component.dataSource.data).toEqual(mockPage.content);
    expect(component.totalItems).toBe(mockPage.totalElements);
    expect(component.paginator.length).toBe(mockPage.totalElements);
    expect(component.paginator.pageIndex).toBe(component.currentPage);
  });

  it('should decrement currentPage and call reloadData when data.content is empty and currentPage > 0', () => {
    const mockPage = {
      content: [],
      totalElements: 0,
    } as any;

    component.currentPage = 2;
    component.selectedStatus = 'All';

    const reloadDataSpy = spyOn(component, 'reloadData');

    component.loadDataSource(mockPage);

    expect(component.currentPage).toBe(1);
    expect(reloadDataSpy).toHaveBeenCalled();
  });

  it('should reload data with "Completed" filter', () => {
    taskServiceSpy.getPageableTasks.and.returnValue(
      of({
        content: [{ id: 1, description: 'Task 1', completed: true }],
        totalElements: 1,
      } as any)
    );
    component.reloadData('Completed', true);
    expect(component.selectedStatus).toBe('Completed');
    expect(taskServiceSpy.getPageableTasks).toHaveBeenCalledWith(0, 10, true);
  });

  it('should reload data with "All" filter', () => {
    taskServiceSpy.getPageableTasks.and.returnValue(
      of({
        content: [
          { id: 1, description: 'Task 1', completed: true },
          { id: 2, description: 'Task 2', completed: false },
        ],
        totalElements: 2,
      } as any)
    );
    component.reloadData('All', true);
    expect(component.selectedStatus).toBe('All');
    expect(taskServiceSpy.getPageableTasks).toHaveBeenCalledWith(0, 10);
  });

  it('should open dialog and create new task', fakeAsync(() => {
    taskServiceSpy.createTask.and.returnValue(of({} as any));
    component.createNewTask();
    tick(); // Simule le retour du dialog et la création
    flush(); // Nettoie tous les timers restants (snackBar, animations)
    expect(dialogSpy.open).toHaveBeenCalled();
    expect(taskServiceSpy.createTask).toHaveBeenCalledWith(task);
  }));

  it('should open dialog and update a task', fakeAsync(() => {
    const taskToUpdate = {
      id: 1,
      description: 'Test To Update',
      completed: true,
    };

    taskServiceSpy.updateTask.and.returnValue(of({} as any));
    component.updateTask(taskToUpdate);
    tick();
    flush();
    expect(dialogSpy.open).toHaveBeenCalled();
    expect(taskServiceSpy.updateTask).toHaveBeenCalledWith(1, task);
  }));
  it('should handle pagination change', () => {
    const event = { pageIndex: 1, pageSize: 20 };
    component.onPageChange(event);
    expect(component.currentPage).toBe(1);
    expect(component.pageSize).toBe(20);
  });

  it('should return correct modal config', () => {
    const task = {
      id: 1,
      description: 'Test task',
      completed: false,
    };

    const result = component.prepareModalConfig(task);

    expect(result).toEqual({
      height: 'auto',
      width: '662px',
      disableClose: true,
      data: task,
    });
  });

  it('should notify user', () => {
    component.notifyUser('Update Succefully');
    expect(
      snackBarSpy.open('Update Succefully', 'Close', {
        verticalPosition: 'bottom',
        horizontalPosition: 'right',
      })
    );
  });
});
