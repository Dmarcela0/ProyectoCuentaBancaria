import { Component } from '@angular/core';

@Component({
  selector: 'app-welcome',
  templateUrl: './welcome.component.html',
  styleUrls: ['./welcome.component.scss']
})
export class WelcomeComponent {
  benefits = [
    'Metas de ahorro ilustradas en pesos colombianos',
    'Consejos semanales para familias colombianas',
    'Seguimiento de hábitos con personajes típicos del país'
  ];
}
