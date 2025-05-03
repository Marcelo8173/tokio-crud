import { CommonModule } from '@angular/common';
import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-pagination',
  templateUrl: './pagination.component.html',
  styleUrls: ['./pagination.component.css'],
  imports: [CommonModule]
})
export class PaginationComponent {
  @Input() page: number = 0;          
  @Input() totalPages: number = 0;     
  @Output() pageChanged: EventEmitter<number> = new EventEmitter<number>(); 

  constructor() {}

  changePage(page: number) {
    if (page >= 0 && page < this.totalPages) {
      this.pageChanged.emit(page);  
    }
  }
}
