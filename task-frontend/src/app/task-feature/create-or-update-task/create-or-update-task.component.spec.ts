import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CreateOrUpdateTaskComponent } from './create-or-update-task.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { Task } from '../../shared/models/Task';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('CreateOrUpdateTaskComponent', () => {
  let component: CreateOrUpdateTaskComponent;
  let fixture: ComponentFixture<CreateOrUpdateTaskComponent>;
  let mockDialogRef: jasmine.SpyObj<MatDialogRef<CreateOrUpdateTaskComponent>>;
  const mockTask: Task = {
    id: 1,
    description: 'task',
    completed: false,
  };

  beforeEach(async () => {
    mockDialogRef = jasmine.createSpyObj('MatDialogRef', ['close']);

    await TestBed.configureTestingModule({
      imports: [CreateOrUpdateTaskComponent, NoopAnimationsModule],
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: mockTask },
        { provide: MatDialogRef, useValue: mockDialogRef },
      ],
      schemas: [NO_ERRORS_SCHEMA], // Ignore error of HTML
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateOrUpdateTaskComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component and copy the input data', () => {
    expect(component).toBeTruthy();
    expect(component.currentData).toEqual(mockTask);
    expect(component.currentData).not.toBe(mockTask);
  });

  it('should close the dialog and emit the current data to be saved or updated on save()', () => {
    component.currentData.description = 'Updated task';
    component.currentData.completed = true;
    component.save();
    expect(mockDialogRef.close).toHaveBeenCalledWith(component.currentData);
  });
});
