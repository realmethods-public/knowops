#set( $className = $classObject.getName() )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter(${className})} )
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { Index${className}Component } from './index.component';

describe('Index${className}Component', () => {
  let component: Index${className}Component;
  let fixture: ComponentFixture<Index${className}Component>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule],
      declarations: [ Index${className}Component ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(Index${className}Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
