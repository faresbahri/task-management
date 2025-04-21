import { Component, OnInit, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { TaskManagementService } from '../../shared/http-services/task-management.service';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatDialog } from '@angular/material/dialog';
import { Page } from '../../shared/models/Page';
import { Task } from '../../shared/models/Task';
import { MatButtonModule } from '@angular/material/button';
import { CreateOrUpdateTaskComponent } from '../create-or-update-task/create-or-update-task.component';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [
    FormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatButtonModule,
    MatSnackBarModule,
  ],
  templateUrl: './task-list.component.html',
  styleUrl: './task-list.component.scss',
})
export class TaskListComponent implements OnInit {
  displayedColumns: string[] = ['description', 'status', 'action'];
  selectedStatus: string = 'All';
  dataSource = new MatTableDataSource<any>();
  totalItems: number = 0;
  pageSize: number = 10;
  currentPage: number = 0;
  @ViewChild(MatPaginator) paginator!: MatPaginator; // The '!' tells TypeScript it's initialized

  constructor(
    private readonly taskManagementService: TaskManagementService,
    private readonly matDialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}
  ngOnInit(): void {
    this.taskManagementService
      .getPageableTasks(this.currentPage, this.pageSize)
      .subscribe({
        next: (data) => {
          this.loadDataSource(data);
        },
      });
  }

  loadDataSource(data: Page<Task>) {
    if(data.content.length > 0){
      this.dataSource.data = data.content;
      this.totalItems = data.totalElements;
      if (this.paginator) {
        this.paginator.length = data.totalElements;
        this.paginator.pageIndex = this.currentPage;
      }
    }else{
      if(this.currentPage>0){
        this.currentPage = this.currentPage -1;
        this.reloadData(this.selectedStatus, false)
      }
    }
    
  }

  reloadData(status: string, fromTemplate: boolean) {
    if (fromTemplate) {
      this.pageSize = 10;
      this.currentPage = 0;
      this.paginator.pageIndex = 0;
    }
    this.selectedStatus = status;
    switch (status) {
      case 'All':
        this.taskManagementService
          .getPageableTasks(this.currentPage, this.pageSize)
          .subscribe({
            next: (data) => {
              this.loadDataSource(data);
            },
          });
        break;
      case 'Completed':
        this.taskManagementService
          .getPageableTasks(this.currentPage, this.pageSize, true)
          .subscribe({
            next: (data) => {
              this.loadDataSource(data);
            },
          });
        break;

      case 'Uncompleted':
        this.taskManagementService
          .getPageableTasks(this.currentPage, this.pageSize, false)
          .subscribe({
            next: (data) => {
              this.loadDataSource(data);
            },
          });
        break;
    }
  }

  updateTask(task: any): void {
    this.matDialog
      .open(CreateOrUpdateTaskComponent, {
        ...this.prepareModalConfig(task),
      })
      .afterClosed()
      .subscribe((task) => {
        if (task) {
          this.taskManagementService.updateTask(task.id, task).subscribe({
            next: () => {
              this.notifyUser('Task updated successfully!');
              this.reloadData(this.selectedStatus, false);
            },
            error: () => {
              this.notifyUser('Task not updated: Technical issue!');
            },
          });
        }
      });
  }

  createNewTask() {
    this.matDialog
      .open(CreateOrUpdateTaskComponent, {
        ...this.prepareModalConfig({
          description: '',
          completed: false,
        }),
      })
      .afterClosed()
      .subscribe((task) => {
        if (task) {
          this.taskManagementService.createTask(task).subscribe({
            next: () => {
              const message = 'Task saved successfully!';
              this.notifyUser(message);
              this.reloadData(this.selectedStatus, false);
            },
            error: () => {
              this.notifyUser('Task not saved: Technical issue!');
            },
          });
        }
      });
  }

  notifyUser(message: string) {
    this.snackBar.open(message, 'Close', {
      verticalPosition: 'bottom',
      horizontalPosition: 'right',
    });
  }

  onPageChange(event: any): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.reloadData(this.selectedStatus, false);
  }

  prepareModalConfig(dataValue: Task) {
    return {
      height: 'auto',
      width: '662px',
      disableClose: true,
      data: dataValue,
    };
  }
}
