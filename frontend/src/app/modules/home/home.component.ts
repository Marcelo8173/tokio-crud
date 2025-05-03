import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { UserService } from '../../services/user.service';
import { PaginationComponent } from '../../components/pagination/pagination.component';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  imports: [CommonModule, PaginationComponent],
})
export class HomeComponent implements OnInit {
  users: any[] = [];
  page: number = 0;
  size: number = 10;
  sortBy: string = 'createdAt';
  direction: string = 'desc';
  totalPages: number = 0;
  loading: boolean = false;

  constructor(private userService: UserService, private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers() {
    this.loading = true;

    this.userService.getUsers(this.size, this.page, this.sortBy, this.direction)
    .subscribe({
      next: (res) => {
        this.users = res.content;
        this.totalPages = Math.ceil(res.totalElements / this.size) || 1;
        this.loading = false;
      },
      error: (err) => {
        console.error('Erro ao carregar usuários:', err);
        this.loading = false;
      }
    });
  }

  changePage(p: number) {
    if (p >= 0 && p < this.totalPages) {
      this.page = p;
      this.loadUsers();
    }
  }

  sort(field: string) {
    if (this.sortBy === field) {
      this.direction = this.direction === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortBy = field;
      this.direction = 'asc';
    }
    this.loadUsers();
  }

  goToUser(id: number) {
    this.router.navigate(['/address', id]);
  }
}
