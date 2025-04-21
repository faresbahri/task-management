import { Component, Inject, OnInit } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { Task } from '../../shared/models/Task';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatRadioModule } from '@angular/material/radio';

@Component({
  selector: 'app-create-or-update-task',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatRadioModule,
    MatInputModule,
    MatDialogActions,
    MatButtonModule,
  ],
  templateUrl: './create-or-update-task.component.html',
  styleUrl: './create-or-update-task.component.scss',
})
export class CreateOrUpdateTaskComponent {
  currentData!: Task;
  constructor(
    public readonly dialogRef: MatDialogRef<CreateOrUpdateTaskComponent>,
    @Inject(MAT_DIALOG_DATA) public readonly data: Task
  ) {
    this.currentData = { ...data };
  }
  save(): void {
    this.dialogRef.close(this.currentData);
  }
}
