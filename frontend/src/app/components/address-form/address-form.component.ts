import { Component, Input, Output, EventEmitter, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { FormGroup, FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { filter, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
import { tap, finalize } from 'rxjs/operators';

@Component({
  selector: 'app-address-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  styleUrl: './address-form.component.css',
  templateUrl: './address-form.component.html',
})
export class AddressFormComponent implements OnInit, OnChanges {
  @Input() addressData: any = {};
  @Output() save = new EventEmitter<any>();
  submitted = false;
  addressForm!: FormGroup;
  isLoadingCep = false;

  constructor(private fb: FormBuilder, private http: HttpClient) {
    this.addressForm = this.fb.group({
      logradouro: ['', Validators.required],
      numero: ['', Validators.required],
      complemento: [''],
      bairro: ['', Validators.required],
      cidade: ['', Validators.required],
      estado: ['', Validators.required],
      cep: ['', Validators.required],
    });
  }

  ngOnInit(): void {
    this.addressForm = this.fb.group({
      logradouro: ['', Validators.required],
      numero: ['', Validators.required],
      complemento: [''],
      bairro: ['', Validators.required],
      cidade: ['', Validators.required],
      estado: ['', Validators.required],
      cep: ['', Validators.required],
    });
  
    if (this.addressData) {
      this.addressForm.patchValue(this.addressData);
    }

    this.listenToCepChanges();
  }

  private listenToCepChanges() {
    this.addressForm.get('cep')?.valueChanges.pipe(
      distinctUntilChanged(),
      filter((cep): boolean => {
        const cleanCep = cep.replace(/\D/g, '');
        return cleanCep.length === 8;
      }),
      tap(() => {
        this.isLoadingCep = true; 
        console.log('Loading started');
      }), 
      switchMap((cep: string) => {
        const cleanCep = cep.replace(/\D/g, ''); 
        console.log('Making API call with CEP:', cleanCep);
        return this.http.get(`https://viacep.com.br/ws/${cleanCep}/json/`); 
      }),
      finalize(() => {
        this.isLoadingCep = false;
        console.log('Loading finished');
      }) 
    ).subscribe((data: any) => {
      if (data && !data.erro) {
        this.addressForm.patchValue({
          logradouro: data.logradouro,
          bairro: data.bairro,
          cidade: data.localidade,
          estado: data.uf
        });
      } else {
        console.error('CEP não encontrado ou inválido');
      }
    });
  }
  
  get f() {
    return this.addressForm.controls;
  }


  ngOnChanges(changes: SimpleChanges): void {
    if (changes['addressData'] && changes['addressData'].currentValue && this.addressForm) {
      this.addressForm.patchValue(changes['addressData'].currentValue);
    }
  }

  initForm() {
    this.addressForm = this.fb.group({
      logradouro: ['', Validators.required],
      numero: ['', Validators.required],
      complemento: [''],
      bairro: ['', Validators.required],
      cidade: ['', Validators.required],
      estado: ['', Validators.required],
      cep: ['', Validators.required],
    });

    if (this.addressData) {
      this.addressForm.patchValue(this.addressData);
    }
  }

  onSubmit() {
    this.submitted = true;
  
    Object.keys(this.addressForm.controls).forEach(field => {
      const control = this.addressForm.get(field);
      if (control) {
        control.markAsTouched();
      }
    });
  
    if (this.addressForm.invalid) {
      return;
    }
  
    if (this.addressForm.valid) {
      this.save.emit(this.addressForm.value);
    }
  }
  
}
