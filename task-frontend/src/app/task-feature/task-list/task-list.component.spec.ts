import {
  ComponentFixture,
  TestBed,
  fakeAsync,
  flush,
  tick,
} from '@angular/core/testing';
import { TaskListComponent } from './task-list.component';
import { MatSnackBarModule } from '@angular/material/snack-bar';
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

  beforeEach(async () => {
    const taskSpy = jasmine.createSpyObj('TaskManagementService', [
      'getPageableTasks',
      'createTask',
      'updateTask',
    ]);
    const dialogMock = jasmine.createSpyObj('MatDialog', ['open']);

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
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TaskListComponent);
    component = fixture.componentInstance;
    taskServiceSpy = TestBed.inject(
      TaskManagementService
    ) as jasmine.SpyObj<TaskManagementService>;
    dialogSpy = TestBed.inject(MatDialog) as jasmine.SpyObj<MatDialog>;

    taskServiceSpy.getPageableTasks.and.returnValue(
      of({ content: [], totalElements: 0 } as any)
    );
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should load tasks on init', () => {
    expect(taskServiceSpy.getPageableTasks).toHaveBeenCalledWith(0, 10);
    expect(component.dataSource.data.length).toBe(0);
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

  it('should open dialog and create new task', fakeAsync(() => {
    const task = { id: 1, description: 'Test', completed: false };
    const afterClosedSpy = jasmine.createSpyObj({ afterClosed: of(task) });
    dialogSpy.open.and.returnValue(afterClosedSpy);
    taskServiceSpy.createTask.and.returnValue(of({} as any));

    component.createNewTask();
    tick(); // Simule le retour du dialog et la création
    flush(); // Nettoie tous les timers restants (snackBar, animations)
    expect(dialogSpy.open).toHaveBeenCalled();
    expect(taskServiceSpy.createTask).toHaveBeenCalledWith(task);
  }));

  it('should handle pagination change', () => {
    const event = { pageIndex: 1, pageSize: 20 };
    component.onPageChange(event);
    expect(component.currentPage).toBe(1);
    expect(component.pageSize).toBe(20);
  });
});
