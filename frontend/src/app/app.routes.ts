import { Routes } from '@angular/router';
import { LoginComponent } from './modules/login/login.component';
import { SignupComponent } from './modules/signup/signup.component';
import { HomeComponent } from './modules/home/home.component';
import { AuthGuardService } from './services/auth.guard.service'
import { AddressComponent } from './modules/address/address.component';

export const routes: Routes = [
    {
        path: '',
        component: LoginComponent
    },
    { path: 'signup', component: SignupComponent },
    { path: 'home', component: HomeComponent, canActivate: [AuthGuardService] },
    { path: 'address/:id', component: AddressComponent, canActivate: [AuthGuardService] },
    { path: '', redirectTo: '', pathMatch: 'full' },
    { path: '**', redirectTo: '' }
];
