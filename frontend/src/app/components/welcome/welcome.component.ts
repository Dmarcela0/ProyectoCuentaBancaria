import { Component } from '@angular/core';

@Component({
  selector: 'app-welcome',
  templateUrl: './welcome.component.html',
  styleUrls: ['./welcome.component.scss']
})
export class WelcomeComponent {
  benefits = [
    'Metas de ahorro ilustradas en pesos colombianos',
    'Respaldo y contenidos financieros del Banco Caja Social',
    'Retos para niñas, niños y adolescentes de 0 a 17 años'
  ];
}
