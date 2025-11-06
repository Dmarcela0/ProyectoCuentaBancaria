import { Component } from '@angular/core';

@Component({
  selector: 'app-welcome',
  templateUrl: './welcome.component.html',
  styleUrls: ['./welcome.component.scss']
})
export class WelcomeComponent {
  benefits = [
    'Respaldo y contenidos financieros del Banco Caja Social',
    'Retos para niñas, niños y adolescentes de 0 a 17 años'
  ];
}
