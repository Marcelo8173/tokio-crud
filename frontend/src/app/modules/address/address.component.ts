import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { PaginationComponent } from '../../components/pagination/pagination.component';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AddressService } from '../../services/address.service';
import { ActivatedRoute } from '@angular/router';
import { ModalComponent } from '../../components/modal/modal.component';
import { AddressFormComponent } from '../../components/address-form/address-form.component';

@Component({
  selector: 'app-address',
  imports: [PaginationComponent, CommonModule, ModalComponent, AddressFormComponent],
  templateUrl: './address.component.html',
  styleUrl: './address.component.css'
})
export class AddressComponent implements OnInit {
  address: any[] = [];
  page: number = 0;
  size: number = 10;
  sortBy: string = 'createdAt';
  direction: string = 'desc';
  totalPages: number = 0;
  loading: boolean = false;
  id!: string;
  showModalEdit = false;
  showModalDelete = false;
  selectedAddressToDelete: any = null;
  selectedAddressToEdit: any = null;
  addModalOpen = false;

  constructor(private addressService: AddressService, private http: HttpClient, private router: Router, private route: ActivatedRoute) { }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    this.id = id || '';
    this.loadEnderecos(id || '');
  }

  loadEnderecos(id: string) {
    this.loading = true;


    this.addressService.getAddressById(this.size, this.page, this.sortBy, this.direction, id).subscribe({
      next: (res) => {
        this.address = res.content;
        this.totalPages = Math.ceil(res.totalElements / this.size) || 1;
        this.loading = false;
      },
      error: (err) => {
        console.error('Erro ao carregar endereços:', err);
        this.loading = false;
      }
    });
  }

  changePage(p: number) {
    if (p >= 0 && p < this.totalPages) {
      this.page = p;
      this.loadEnderecos(this.id);
    }
  }

  sort(field: string) {
    if (this.sortBy === field) {
      this.direction = this.direction === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortBy = field;
      this.direction = 'asc';
    }
    this.loadEnderecos(this.id);
  }

  openModalEdit(address: any) {
    this.selectedAddressToEdit = address;
    this.showModalEdit = true;
  }

  closeModalEdit(): void {
    this.selectedAddressToEdit = null;
    this.showModalEdit = false;
  }


  openModalDelete(address: any) {
    this.selectedAddressToDelete = address;
    this.showModalDelete = true;
  }

  closeModalDelete() {
    this.showModalDelete = false;
  }

  confirmDelete(): void {
    this.addressService.deleteById(this.selectedAddressToDelete.id).subscribe({
      next: () => {
        this.loadEnderecos(this.id);
        this.closeModalDelete();
      },
      error: (err) => {
        console.error('Erro ao deletar endereço', err);
      }
    });
  }

  onSaveEdit(updatedData: any): void {
    const updated = { ...this.selectedAddressToEdit, ...updatedData };
    this.addressService.updateAddress(updated.id, updated).subscribe({
      next: () => {
        this.loadEnderecos(this.id);
        this.closeModalEdit();
      },
      error: (err) => {
        console.error('Erro ao editar endereço', err);
      }
    });
  }

  openModalCreate(): void {
    this.addModalOpen = true;
  }

  closeModalCreate(): void {
    this.addModalOpen = false;

  }

  onSaveModalCreate(createAddress: any): void {
      this.addressService.createAddress(createAddress).subscribe({
        next: () => {
          this.loadEnderecos(this.id);
          this.closeModalCreate();
        },
        error: (err) => {
          console.error('Erro ao adicionar endereço', err);
        }
      });
  }
} 
